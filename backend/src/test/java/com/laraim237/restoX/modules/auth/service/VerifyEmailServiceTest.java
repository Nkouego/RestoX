package com.laraim237.restoX.modules.auth.service;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildToken;
import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.security.authentication.BadCredentialsException;

import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.modules.audit.AuditService;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.VerifyEmailRequest;
import com.laraim237.restoX.modules.auth.entity.AccessToken;
import com.laraim237.restoX.modules.auth.enums.TokenType;
import com.laraim237.restoX.modules.auth.repository.AccessTokenRepository;
import com.laraim237.restoX.modules.auth.repository.UserRepository;
import com.laraim237.restoX.modules.auth.service.Authservice;
import com.laraim237.restoX.modules.auth.service.EmailService;
import com.laraim237.restoX.modules.auth.service.JwtService;
import com.laraim237.restoX.modules.auth.service.RefreshTokenService;
import com.laraim237.restoX.modules.auth.service.Impl.AuthServiceImpl;
import com.laraim237.restoX.modules.user.User;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class VerifyEmailServiceTest {
	@Mock private UserRepository userRepository;
	@Mock private AccessTokenRepository accessTokenRepository;
	@Mock private AuditService auditService;
	@Mock private JwtService jwtService;
	@Mock private RefreshTokenService refreshTokenService;
	@Mock private EmailService emailService;
	@Mock private HttpServletRequest httpRequest;
	
	@InjectMocks
	private AuthServiceImpl  authService;
	
	private VerifyEmailRequest request;
	
	@BeforeEach
	void setUp() {
		request = new VerifyEmailRequest("example@gmail.com", "123456");
	}
	
	@Test
	@DisplayName("Code valide -> active le compte, retourne JWT + refreshToken")
	void verify_email_success() {
		User user = buildUser(false);
		AccessToken accessToken = buildToken(user, TokenType.REGISTRATION, false);
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.REGISTRATION)).thenReturn(Optional.of(accessToken));
		when(jwtService.generateToken(user)).thenReturn("jwt-token");
		when(refreshTokenService.generateRefreshToken(user)).thenReturn("refresh-token");
		
		AuthResponse response = authService.verifyEmail(request, httpRequest);
		
		assertThat(response.getToken()).isEqualTo("jwt-token");
		assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
		assertThat(user.isEnabled()).isTrue();
		verify(accessTokenRepository).delete(accessToken);
		verify(userRepository).save(user);
		verify(emailService).sendWelcomeEmail(user);
		verify(auditService).log(any(), any(), any(), any(), any(), any(), any(), any());
	}
	
	@Test
	@DisplayName("Code expiré -> OTPException")
	void verifyEmail_expiredCode_throwsOTPException() {
		User user = buildUser(false);
		AccessToken accessToken = buildToken(user, TokenType.REGISTRATION, true);
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.REGISTRATION)).thenReturn(Optional.of(accessToken));
		
		assertThatThrownBy( ()-> authService.verifyEmail(request, httpRequest))
				.isInstanceOf(OTPException.class)
				.hasMessageContaining("Expired code");
	}
	
	@Test
	@DisplayName("Code invalide → OTPException  + token supprimé ")
	void verifyEmail_invalidCode_throwsOTPException() {
		
		VerifyEmailRequest badRequest = new VerifyEmailRequest("example@gmail.com", "000000");
		
		User user = buildUser(false);
		AccessToken accessToken = buildToken(user, TokenType.REGISTRATION, false);
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.REGISTRATION)).thenReturn(Optional.of(accessToken));
		
		assertThatThrownBy( ()-> authService.verifyEmail(badRequest, httpRequest))
		.isInstanceOf(OTPException.class)
		.hasMessageContaining("Invalid code");
		
		verify(accessTokenRepository).delete(accessToken);
	}
	
	@Test
    @DisplayName("Email inconnu → BadCredentialsException")
    void verifyEmail_unknownEmail_throwsBadCredentials() {
        when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> authService.verifyEmail(request, httpRequest))
                .isInstanceOf(BadCredentialsException.class);
    }
	
	@Test
	@DisplayName("Aucun token trouvé → OTPException")
	void verifyEmail_noTokenFound_throwsBadCredentials() {
		User user = buildUser(false);
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.REGISTRATION)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> authService.verifyEmail(request, httpRequest))
		.isInstanceOf(OTPException.class)
		.hasMessageContaining("Invalid or expired code");
	}

}
