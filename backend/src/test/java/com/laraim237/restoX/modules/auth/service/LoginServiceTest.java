package com.laraim237.restoX.modules.auth.service;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.laraim237.restoX.modules.audit.AuditService;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.LoginRequest;
import com.laraim237.restoX.modules.auth.repository.UserRepository;
import com.laraim237.restoX.modules.auth.service.Impl.AuthServiceImpl;
import com.laraim237.restoX.modules.user.User;
import com.laraim237.restoX.modules.user.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {
	@Mock private UserRepository userRepository;
	@Mock private AuthenticationManager authenticationManager;
	@Mock private AuditService auditService;
	@Mock private JwtService jwtService;
	@Mock private RefreshTokenService refreshTokenService;
	@Mock private HttpServletRequest httpRequest;

	@InjectMocks
	private AuthServiceImpl  authService;
	
	private LoginRequest request;
	
	@BeforeEach
	void setUp() {
		request = new LoginRequest("example@gmail.com", "12345678");
	}
	
	@Test
	@DisplayName("Credentials corrects → retourne JWT + refreshToken")
	void login_success() {
		User user = buildUser(true);
		Authentication auth = mock(Authentication.class);
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		
		when(jwtService.generateToken(user)).thenReturn("jwt-token");
		when(refreshTokenService.generateRefreshToken(user)).thenReturn("refresh-token");
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
		when(auth.getPrincipal()).thenReturn(userDetails);
		
        AuthResponse response = authService.login(request, httpRequest);
		
		assertThat(response.getToken()).isEqualTo("jwt-token");
		assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
	}
	
	@Test
	@DisplayName("Identifiants invalides (email ou password) → BadCredentialsException")
	void login_invalidCredentials_throwsBadCredentialsException() {
		doThrow(new BadCredentialsException("invalid credentials"))
		.when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		
		assertThatThrownBy(() -> authService.login(request, httpRequest))
		.isInstanceOf(BadCredentialsException.class);
		
		verify(jwtService, never()).generateToken(any());
		verify(refreshTokenService, never()).generateRefreshToken(any());
	}
	
	@Test
	@DisplayName("compte desactive → DisabledException")
	void login_disabledAccount_DisabledException() {
		doThrow(new DisabledException("User is disabled"))
		.when(authenticationManager).authenticate(any());
		
		assertThatThrownBy(() -> authService.login(request, httpRequest))
		.isInstanceOf(DisabledException.class);
		
		verify(jwtService, never()).generateToken(any());
		verify(refreshTokenService, never()).generateRefreshToken(any());
	}
	

}
