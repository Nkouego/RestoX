package com.laraim237.restoX.modules.auth.service;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.laraim237.restoX.common.Exception.RefreshTokenException;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.RefreshTokenRequest;
import com.laraim237.restoX.entity.RefreshToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.JwtService;
import com.laraim237.restoX.service.RefreshTokenService;
import com.laraim237.restoX.service.Impl.AuthServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
	@Mock private RefreshTokenService refreshTokenService; 
	@Mock private JwtService jwtService; 
	@Mock private AuditService auditService; 
	@Mock private HttpServletRequest httpRequest; 
	
	@InjectMocks
	private AuthServiceImpl authService;
	
	private RefreshTokenRequest request;
	
	@BeforeEach
	void setUp() {
		request = new RefreshTokenRequest("refresh-token");
	}
	
	@Test
	@DisplayName("Token valide -> nouveau JWT + nouveau refreshToken")
	void refreshToken_success() {
		User user = buildUser(true);
		RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .user(user)
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();
		
		when(refreshTokenService.validateRefreshToken(request.refreshToken())).thenReturn(refreshToken);
		when(jwtService.generateToken(user)).thenReturn("jwt");
		when(refreshTokenService.generateRefreshToken(user)).thenReturn("new-refresh-token");
		
		AuthResponse response = authService.refreshToken(request, httpRequest);
		assertThat(response.getToken()).isEqualTo("jwt");
		assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
	}
	
	@Test
	void refreshToken_expiredOrRevoked_throwsRefreshTokenException() {
		doThrow(new RefreshTokenException("token expiré ou revoqué"))
		.when(refreshTokenService).validateRefreshToken(request.refreshToken());
		
		assertThatThrownBy(() -> authService.refreshToken(request, httpRequest))
        .isInstanceOf(RefreshTokenException.class);
	}
}
