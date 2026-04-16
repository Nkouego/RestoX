package com.laraim237.restoX.modules.auth.service;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildToken;
import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.ForgotPasswordRequest;
import com.laraim237.restoX.dto.AuthDto.ResendCodeRequest;
import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;
import com.laraim237.restoX.repository.AccessTokenRepository;
import com.laraim237.restoX.repository.UserRepository;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.EmailService;
import com.laraim237.restoX.service.Impl.AuthServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordServiceTest {
	
	@Mock private UserRepository userRepository;
	@Mock private AccessTokenRepository accessTokenRepository;
	@Mock private HttpServletRequest httpRequest;
	@Mock private EmailService emailService;
	@Mock private AuditService auditService;
	
	@InjectMocks
	private AuthServiceImpl authservice;
	
	private ForgotPasswordRequest request;
	
	@BeforeEach
	void setUp() {
		request = new ForgotPasswordRequest("example@gmail.com");
	}
	
	@Test
	@DisplayName("email valide -> genere code, envoie l'email")
	void forgotPassword_success() {
		User user = buildUser(false);
		AccessToken accessToken = buildToken(user, TokenType.REGISTRATION, false);
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)).thenReturn(Optional.of(accessToken));
		
		AuthResponse response = authservice.forgotPassword(request, httpRequest);
		
		assertThat(response.getMessage()).contains("If this email exists,");
	
		verify(accessTokenRepository).delete(accessToken);
		verify(accessTokenRepository).save(any());
		verify(accessTokenRepository).flush();
		verify(emailService).sendPasswordResetEmail(eq(user), any());
		verify(auditService).log(any(), any(), any(), any(), any(), any(), any(), any());
	}
	
	@Test
	@DisplayName("email invalide -> renvoie un message generic")
	void forgotPassword_unknowEmail_returnsGenericMessage() {
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.empty());
		
		AuthResponse response = authservice.forgotPassword(request, httpRequest);
		
		assertThat(response.getMessage()).contains("If this email exists,");
		verify(emailService, never()).sendVerificationEmail(any(), any());
	}
}
