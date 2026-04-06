package com.laraim237.restoX.modules.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import com.laraim237.restoX.modules.audit.AuditService;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.service.Impl.AuthServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class LogoutServiceTest {
	@Mock RefreshTokenService refreshTokenService;
	@Mock HttpServletRequest httpRequest;
	@Mock AuditService auditService;
	
	@InjectMocks
	private AuthServiceImpl authService;
	
	@Test
	@DisplayName("deconnexion")
	void logout_success() {
		Authentication authentication = mock(Authentication.class);
		Jwt jwt = mock(Jwt.class);
		
		when(authentication.getPrincipal()).thenReturn(jwt);
		when(jwt.getClaim("userId")).thenReturn(1L);
		
		AuthResponse response = authService.logout(authentication, httpRequest);
		
		assertThat(response.getMessage()).contains("Logged out");
		verify(refreshTokenService).revokeAllTokens(1L);
	}

}
