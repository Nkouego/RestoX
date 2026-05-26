package com.laraim237.restoX.service.Impl;

import java.net.http.HttpRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laraim237.restoX.common.Exception.AccountAlreadyExistsException;
import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.common.utils.OtpUtils;
import com.laraim237.restoX.config.security.UserDetailsImpl;
import com.laraim237.restoX.dto.AuthDto;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.ForgotPasswordRequest;
import com.laraim237.restoX.dto.AuthDto.LoginRequest;
import com.laraim237.restoX.dto.AuthDto.RefreshTokenRequest;
import com.laraim237.restoX.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.dto.AuthDto.ResendCodeRequest;
import com.laraim237.restoX.dto.AuthDto.ResetPasswordRequest;
import com.laraim237.restoX.dto.AuthDto.VerifyEmailRequest;
import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.RefreshToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.AuditAction;
import com.laraim237.restoX.enums.TokenType;
import com.laraim237.restoX.mapper.AuthMapper;
import com.laraim237.restoX.repository.AccessTokenRepository;
import com.laraim237.restoX.repository.UserRepository;
import com.laraim237.restoX.service.AccessTokenService;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.AuthService;
import com.laraim237.restoX.service.EmailService;
import com.laraim237.restoX.service.JwtService;
import com.laraim237.restoX.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {


	private final UserRepository userRepository;
	private final AccessTokenRepository accessTokenRepository;
	
	private final AuthMapper authMapper;
	
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	
	private final JwtService jwtService;
	private final EmailService emailService;
	private final AuditService auditService;
	private final PasswordService passwordService;
	private final AccessTokenService accessTokenService;
	private final RefreshTokenService refreshTokenService;	
		
	@Override
	public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {

		//1.verifie si l'email existe deja
		if(userRepository.existsByEmail(request.email())) {
			throw new AccountAlreadyExistsException("Account already exists");
		}
		
		//2.Cree le user
		User user = authMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.password()));
		userRepository.save(user);
		
		// 3. Générer le token de vérification email
		AccessToken accessToken = OtpUtils.generateOTP(user, TokenType.REGISTRATION, 15);
		accessTokenRepository.save(accessToken);
		
		 // 4. Envoyer l'email de vérification
        emailService.sendVerificationEmail(user, accessToken);
        
        //5.Audit
        auditService.log(AuditAction.REGISTER, user.getId(), "User", null, null, null, user.getId(), httpRequest);
		
        return AuthDto.AuthResponse.builder()
        		.enabled(user.isEnabled())
                .message("Account created successfully. Please check your email to confirm your account.")
                .build();
	}

	@Override
	public AuthResponse verifyEmail(VerifyEmailRequest request, HttpServletRequest httpRequest) {
		
		//	1. Trouver le user
		User user = userRepository.findByEmailIgnoreCase(request.email())
				    .orElseThrow(()-> new BadCredentialsException("Invalid credentials"));
		
		//	2.Trouver le TokenType actif
		AccessToken accessToken = accessTokenRepository.findByUserAndType(user, TokenType.REGISTRATION)
									.orElseThrow(() -> new OTPException("Invalid or expired code"));
		
		//	3.Verifier l'expiration
		if(accessToken.isExpired()) {
			throw new OTPException("Expired code, please request a new one");
		}
		
		//	4.Verifier code
		if(!request.code().equals(accessToken.getToken())) {
			throw new OTPException("Invalid code");
		}
		
		//	5.Active le compte
		user.setEnabled(true);
		userRepository.save(user);
		
		//	6.supprimer le token utilisé
		accessTokenRepository.delete(accessToken);
		
		//	7.generer le jwt
		String jwt = jwtService.generateToken(user, null);
		
		//	8.Generer le refresh token
		String refreshToken = refreshTokenService.generateRefreshToken(user);
		
		//10.Envoie un email de bienvenu
		emailService.sendWelcomeEmail(user);
		
		//	9.Audit
		auditService.log(AuditAction.EMAIL_VERIFIED, user.getId(), "User", null, null, null, user.getId(), httpRequest);
		
		AuthResponse response = authMapper.toAuthResponse(user);
		
		response.setToken(jwt);
		response.setRefreshToken(refreshToken);
		return response;
	}

	@Override
	public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
		
		//1.Laisse Spring security s'occuper de la connexion
	    Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password())
		);

		// 2. Récupère l'utilisateur depuis l'objet d'authentification
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	    User user = userDetails.getUser();
	    
		//3.generer le jwt
		String jwt = jwtService.generateToken(user, null);
		
		//4.Generer le refresh token
		String refreshToken = refreshTokenService.generateRefreshToken(user);
		
		//5.Audit
		auditService.log(AuditAction.LOGIN_SUCCESS, user.getId(), "User", null, null, null, user.getId(), httpRequest);
		
		AuthResponse response = authMapper.toAuthResponse(user);
		
		response.setToken(jwt);
		response.setRefreshToken(refreshToken);
		return response;
		
		
	}

	@Override
	public AuthResponse resendCode(ResendCodeRequest request, HttpServletRequest httpRequest) {
		// 1.Trouve le user
		userRepository.findByEmailIgnoreCase(request.email()).ifPresent((user)->{
			
			//2.Verifie que le compte n'est pas deja actif
			if(user.isEnabled()) {
				throw new OTPException("Account is already verified");	
			}
			
			//3.genere le token d'access
			AccessToken accessToken = accessTokenService.generate(user, TokenType.PASSWORD_RESET, 15);
			
			// 4. Envoyer l'email
			emailService.sendVerificationEmail(user, accessToken);
			
			//6.Audit
			auditService.log(AuditAction.EMAIL_VERIFICATION_RESENT, user.getId(), "User", null, null, null, user.getId(), httpRequest);
			
		});
		
		return AuthResponse.builder()
				.message("If this email exists, a new verification code has been sent to your email.")
				.build();
		
	}

	@Override
	public AuthResponse forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
		  // 1.Trouve le user
		  userRepository.findByEmailIgnoreCase(request.email()).ifPresent((user)->
			  passwordService.sendPasswordResetCode(user, httpRequest)
	  );

		return AuthResponse.builder()
				.message("If this email exists, a password reset code has been sent to your email.")
				.build();
	}

	@Override
	public AuthResponse resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
		// 1.Trouve le user
		User user = userRepository.findByEmailIgnoreCase(request.email())
						.orElseThrow(()-> new BadCredentialsException("Invalid credentials"));
		
		passwordService.confirmPasswordReset(user, request.code(), request.newPassword(), httpRequest);
		return AuthResponse.builder()
				.message("Password reset successfully")
				.build();
	}

	@Override
	public AuthResponse logout(Authentication authentication, HttpServletRequest httpRequest) {
		//1. On recupere le le userId du jwt
		Jwt jwt = (Jwt)authentication.getPrincipal();
		String userId = jwt.getClaim("userId");
		
		//2.Revoquer tout les refresh tokens
		refreshTokenService.revokeAllTokens(userId);
		
		//3. Audit
	    auditService.log(AuditAction.LOGOUT, userId, "user",
	        null, null, null, userId, httpRequest);

	    return AuthResponse.builder()
	        .message("Logged out successfully.")
	        .build();
		
	}

	@Override
	public AuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
		//1. On verifie le refresh token
		RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.refreshToken());
		
		//2.On trouve le user
		User user = refreshToken.getUser();
		
		//3.genere un nouveau jwt
		String jwt = jwtService.generateToken(user, null);
		
		//4.genre un nouveau refresh token
		String newRefreshToken = refreshTokenService.generateRefreshToken(user);
		
		// 5. Audit
	    auditService.log(AuditAction.TOKEN_REFRESHED, user.getId(),
	        "user", null, null, null, user.getId(), httpRequest);

	    return AuthResponse.builder()
	        .token(jwt)
	        .refreshToken(newRefreshToken)
	        .build();
	}
}
