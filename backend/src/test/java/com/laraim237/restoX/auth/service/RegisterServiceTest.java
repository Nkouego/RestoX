package com.laraim237.restoX.auth.service;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.laraim237.restoX.common.Exception.AccountAlreadyExistsException;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.mapper.AuthMapper;
import com.laraim237.restoX.repository.AccessTokenRepository;
import com.laraim237.restoX.repository.RestaurantRepository;
import com.laraim237.restoX.repository.RestaurantUserRepository;
import com.laraim237.restoX.repository.UserRepository;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.EmailService;
import com.laraim237.restoX.service.Impl.AuthServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {
	
	@Mock private UserRepository userRepository;
	@Mock private RestaurantRepository restaurantRepository;
	@Mock private RestaurantUserRepository restaurantUserRepository;
	@Mock private AuthMapper registerMapper;
	@Mock private EmailService emailService;
	@Mock private AuditService auditService;
	@Mock private PasswordEncoder passwordEncoder;
	@Mock private AccessTokenRepository accessTokenRepository;
	@Mock private HttpServletRequest httpRequest;
	
	@InjectMocks
	private AuthServiceImpl  authservice;
	
	private RegisterRequest request;
	
	@BeforeEach
	void setUp() {
		request = new RegisterRequest("Sam", "Kegne", "samkegne@example.com", "password");
	}
	
	@Test
	@DisplayName("cree user, restaurant, envoie mail")
	void register_newEmail_success() {
		User user = buildUser(false);
		
		Restaurant restaurant = Restaurant.builder().id("1").name("mon Resto").build();
		
		when(userRepository.existsByEmail(request.email())).thenReturn(false);
		when(registerMapper.toUser(request)).thenReturn(user);
		when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
		when(accessTokenRepository.save(any())).thenAnswer(i-> i.getArgument(0));
		
		AuthResponse response = authservice.register(request, httpRequest);
		
		assertThat(response.getMessage()).contains("Account created successfully.");
		verify(userRepository).save(user);
		verify(accessTokenRepository).save(any());
		verify(emailService).sendVerificationEmail(eq(user), any());
		verify(auditService).log(any(), any(), any(), any(), any(), any(), any(), any());	
	}
	
	@Test
	@DisplayName("Email déjà existant → AccountAlreadyExistsException")
	void register_emailExists_throwsException() {
		when(userRepository.existsByEmail(request.email())).thenReturn(true);
		
		assertThatThrownBy( () -> authservice.register(request, httpRequest))
				.isInstanceOf(AccountAlreadyExistsException.class)
				.hasMessageContaining("Account already exists");
		
		verify(userRepository, never()).save(any());
		verify(emailService, never()).sendVerificationEmail(any(), any());
		
	}
	


}
