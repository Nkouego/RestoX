package com.laraim237.restoX.modules.auth.service.Impl;

import java.security.SecureRandom;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laraim237.restoX.common.Exception.AccountAlreadyExistsException;
import com.laraim237.restoX.modules.audit.AuditAction;
import com.laraim237.restoX.modules.audit.AuditService;
import com.laraim237.restoX.modules.auth.dto.AuthDto;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.modules.auth.entity.AccessToken;
import com.laraim237.restoX.modules.auth.enums.TokenType;
import com.laraim237.restoX.modules.auth.mapper.RegisterMapper;
import com.laraim237.restoX.modules.auth.repository.AccessTokenRepository;
import com.laraim237.restoX.modules.auth.repository.UserRepository;
import com.laraim237.restoX.modules.auth.service.Authservice;
import com.laraim237.restoX.modules.auth.service.EmailService;
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
public class AuthServiceImpl implements Authservice {
	private final UserRepository userRepository;
	private final RestaurantRepository restaurantRepository;
	private final RestaurantUserRepository restaurantUserRepository;
	private final AccessTokenRepository accessTokenRepository;
	private final RegisterMapper registerMapper;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final AuditService auditService;
	
	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
		System.out.println("EMAIL: " + request.email());
		System.out.println("EXISTS: " + userRepository.existsByEmail(request.email()));
		System.out.println("COUNT: " + userRepository.count());
		
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
}
