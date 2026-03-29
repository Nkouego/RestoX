package com.laraim237.restoX.modules.auth.service.Impl;

import java.security.SecureRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laraim237.restoX.common.Exception.AccountAlreadyExistsException;
import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.common.Exception.UserNotFoundException;
import com.laraim237.restoX.modules.audit.AuditAction;
import com.laraim237.restoX.modules.audit.AuditService;
import com.laraim237.restoX.modules.auth.dto.AuthDto;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.VerifyEmailRequest;
import com.laraim237.restoX.modules.auth.entity.AccessToken;
import com.laraim237.restoX.modules.auth.enums.TokenType;
import com.laraim237.restoX.modules.auth.mapper.RegisterMapper;
import com.laraim237.restoX.modules.auth.repository.AccessTokenRepository;
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
		SecureRandom secureRandom = new SecureRandom();
		String token = Integer.toString(1000 + secureRandom.nextInt(899999));
		AccessToken accessToken = new AccessToken(token, user, 11, TokenType.REGISTRATION);
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
		
//		1. Trouver le user
		User user = userRepository.findByEmailIgnoreCase(request.email())
				    .orElseThrow(()-> new UserNotFoundException("User not found"));
		
//		2.Trouver le TokenType actif
		AccessToken accessToken = accessTokenRepository.findByUserAndType(user, TokenType.REGISTRATION)
									.orElseThrow(() -> new OTPException("Invalid or expired code"));
		
//		3.Verifier l'expiration
		if(accessToken.isExpired()) {
			throw new OTPException("Expired code, please request a new one");
		}
		
//		4.Verifier code
		if(!request.code().equals(accessToken.getToken())) {
			accessTokenRepository.delete(accessToken);
			throw new OTPException("Invalid code");
		}
		
//		5.Active le compte
		user.setEnabled(true);
		userRepository.save(user);
		
//		6.supprimer le token utilisé
		accessTokenRepository.delete(accessToken);
		
//		7.generer le jwt
		String jwt = jwtService.generateToken(user);
		
//		8.Generer le refresh token
		String refreshToken = refreshTokenService.generateRefreshToken(user);
		
//		9.Audit
		auditService.log(AuditAction.EMAIL_VERIFIED, user.getId(), "User", null, null, null, user.getId(), httpRequest);
		
		return AuthResponse.builder()
				.token(jwt)
				.refreshToken(refreshToken)
				.build();
	}
}
