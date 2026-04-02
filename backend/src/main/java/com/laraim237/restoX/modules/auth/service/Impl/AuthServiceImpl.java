package com.laraim237.restoX.modules.auth.service.Impl;

import java.security.SecureRandom;

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
import com.laraim237.restoX.common.Utils.OtpUtils;
import com.laraim237.restoX.modules.audit.AuditAction;
import com.laraim237.restoX.modules.audit.AuditService;
import com.laraim237.restoX.modules.auth.dto.AuthDto;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ForgotPasswordRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.LoginRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RefreshTokenRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ResendCodeRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ResetPasswordRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.VerifyEmailRequest;
import com.laraim237.restoX.modules.auth.entity.AccessToken;
import com.laraim237.restoX.modules.auth.entity.RefreshToken;
import com.laraim237.restoX.modules.auth.enums.TokenType;
import com.laraim237.restoX.modules.auth.mapper.RegisterMapper;
import com.laraim237.restoX.modules.auth.repository.AccessTokenRepository;
import com.laraim237.restoX.modules.auth.repository.RefreshTokenRepository;
import com.laraim237.restoX.modules.auth.repository.UserRepository;
import com.laraim237.restoX.modules.auth.service.Authservice;
import com.laraim237.restoX.modules.auth.service.EmailService;
import com.laraim237.restoX.modules.auth.service.JwtService;
import com.laraim237.restoX.modules.auth.service.RefreshTokenService;
import com.laraim237.restoX.modules.restaurant.Restaurant;
import com.laraim237.restoX.modules.restaurant.RestaurantRepository;
import com.laraim237.restoX.modules.restaurant.RestaurantRole;
import com.laraim237.restoX.modules.restaurant.RestaurantUser;
import com.laraim237.restoX.modules.restaurant.RestaurantUserRepository;
import com.laraim237.restoX.modules.user.User;
import com.laraim237.restoX.modules.user.enums.StaffStatus;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements Authservice {


	private final UserRepository userRepository;
	private final RestaurantRepository restaurantRepository;
	private final RestaurantUserRepository restaurantUserRepository;
	private final AccessTokenRepository accessTokenRepository;
	
	private final RegisterMapper registerMapper;
	
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	
	private final EmailService emailService;
	private final AuditService auditService;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;	
		
	@Override
	public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {

		//1.verifie si l'email existe deja
		if(userRepository.existsByEmail(request.email())) {
			throw new AccountAlreadyExistsException("Account already exists");
		}
		
		//2.Cree le user
		User user = registerMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.password()));
		userRepository.save(user);
		
		//3.Cree le restaurant
		Restaurant restaurant = registerMapper.toRestaurant(request);
		restaurantRepository.save(restaurant);
		
		//4.Lier le user au restaurant avec le rôle ADMIN
		RestaurantUser restaurantUser = RestaurantUser.builder()
				.user(user)
				.restaurant(restaurant)
				.status(StaffStatus.PENDING)
				.role(RestaurantRole.ADMIN)
				.build();
		restaurantUserRepository.save(restaurantUser);
		
		// 5. Générer le token de vérification email
		AccessToken accessToken = OtpUtils.generateAndSaveOTP(user, TokenType.REGISTRATION);
		accessTokenRepository.save(accessToken);
		
		 // 6. Envoyer l'email de vérification
        emailService.sendVerificationEmail(user, accessToken);
        
        //7.Audit
        auditService.log(AuditAction.REGISTER, user.getId(), "User", null, null, restaurant.getId(), user.getId(), httpRequest);
		
        return AuthDto.AuthResponse.builder()
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
		String jwt = jwtService.generateToken(user);
		
		//	8.Generer le refresh token
		String refreshToken = refreshTokenService.generateRefreshToken(user);
		
		//10/Envoie un email de bienvenu
		emailService.sendWelcomeEmail(user);
		
		//	9.Audit
		auditService.log(AuditAction.EMAIL_VERIFIED, user.getId(), "User", null, null, null, user.getId(), httpRequest);
		
		return AuthResponse.builder()
				.token(jwt)
				.refreshToken(refreshToken)
				.build();
	}

	@Override
	public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
		
		//1.Laisse Spring security s'occuper de la connexion
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password())
		);
		
        //2. Trouver le user
		User user = userRepository.findByEmailIgnoreCase(request.email())
				    .orElseThrow(()-> new BadCredentialsException("Invalid credentials"));
		
		//3.generer le jwt
		String jwt = jwtService.generateToken(user);
		
		//4.Generer le refresh token
		String refreshToken = refreshTokenService.generateRefreshToken(user);
		
		//5.Audit
		auditService.log(AuditAction.LOGIN_SUCCESS, user.getId(), "User", null, null, null, user.getId(), httpRequest);
		
		return AuthResponse.builder()
				.token(jwt)
				.refreshToken(refreshToken)
				.build();
		
	}

	@Override
	public AuthResponse resendCode(ResendCodeRequest request, HttpServletRequest httpRequest) {
		// 1.Trouve le user
		userRepository.findByEmailIgnoreCase(request.email()).ifPresent((user)->{
			
			//2.Verifie que le compte n'est pas deja actif
			if(user.isEnabled()) {
				throw new OTPException("Account is already verified");	
			}
			
			//3.supprime l'ancien token s'il existe
			accessTokenRepository.findByUserAndType(user, TokenType.REGISTRATION)
			.ifPresent(accessTokenRepository::delete);
			
			accessTokenRepository.flush(); // Force le DELETE en base maintenant
			
			//4.Genere un nouveau code
			AccessToken accessToken = OtpUtils.generateAndSaveOTP(user, TokenType.REGISTRATION);
			accessTokenRepository.save(accessToken);
			
			// 5. Envoyer l'email
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
		userRepository.findByEmailIgnoreCase(request.email()).ifPresent((user)->{
			  
		  //2.On supprime l'ancien token si il existe 
		  accessTokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)
		  .ifPresent(accessTokenRepository::delete);
		  
		  accessTokenRepository.flush(); // Force le DELETE en base maintenant
		  
		  //3.Genere un nouveau code
		  AccessToken accessToken = OtpUtils.generateAndSaveOTP(user, TokenType.PASSWORD_RESET);
		  accessTokenRepository.save(accessToken);
		  
		  //4.Envoyer l'email
		  emailService.sendPasswordResetEmail(user, accessToken);
		  
		  //6.Audit
		  auditService.log(AuditAction.PASSWORD_RESET_REQUESTED, user.getId(), "User", null, null, null, user.getId(), httpRequest);
			  
		  });

		return AuthResponse.builder()
				.message("If this email exists, a password reset code has been sent to your email.")
				.build();
	}

	@Override
	public AuthResponse resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
		// 1.Trouve le user
		User user = userRepository.findByEmailIgnoreCase(request.email())
						.orElseThrow(()-> new BadCredentialsException("Invalid credentials"));
		
		//2.Trouver le TokenType actif
		AccessToken accessToken = accessTokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)
									.orElseThrow(() -> new OTPException("Invalid or expired code"));
		
//		//3.Verifier l'expiration
		if(accessToken.isExpired()) {
			throw new OTPException("Expired code, please request a new one");
		}
		
//		//4.Verifier code
		if(!request.code().equals(accessToken.getToken())) {
			throw new OTPException("Invalid code");
		}
		
		//5.change le mot de passe
		user.setPassword(passwordEncoder.encode(request.newPassword()));;
		userRepository.save(user);
		
//		//6.supprimer le token utilisé
		accessTokenRepository.delete(accessToken);
		
		 //7.Audit
	    auditService.log(AuditAction.PASSWORD_RESET_CONFIRMED, user.getId(), "User", null, null, null, user.getId(), httpRequest);
	    
		return AuthResponse.builder()
				.message("Password reset successfully")
				.build();
	}

	@Override
	public AuthResponse logout(Authentication authentication, HttpServletRequest httpRequest) {
		//1. On recupere le le userId du jwt
		Jwt jwt = (Jwt)authentication.getPrincipal();
		Long userId = jwt.getClaim("userId");
		
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
		String jwt = jwtService.generateToken(user);
		
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
