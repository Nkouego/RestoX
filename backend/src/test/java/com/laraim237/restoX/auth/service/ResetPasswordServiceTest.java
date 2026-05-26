package com.laraim237.restoX.auth.service;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildToken;
import static com.laraim237.restoX.commons.utils.AuthTestHelpers.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.ResetPasswordRequest;
import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;
import com.laraim237.restoX.repository.AccessTokenRepository;
import com.laraim237.restoX.repository.UserRepository;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.Impl.AuthServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordServiceTest {
	@Mock private UserRepository userRepository;
	@Mock private AccessTokenRepository accessTokenRepository;
	@Mock private PasswordEncoder passwordEncoder;
	@Mock private HttpServletRequest httpRequest;
	@Mock private AuditService auditService;
	
	@InjectMocks
	private AuthServiceImpl authService;
	
	private ResetPasswordRequest request;

	
	@BeforeEach
	void setUp() {
		request = new ResetPasswordRequest("exemple@gmail.com", "123456", "newPassword");
	}
	
	@Test
	@DisplayName("Code valide -> mot de passe changé, token supprimé")
	void resetPassword_success() {
		User user = buildUser(true);
		AccessToken accessToken = buildToken(user, TokenType.PASSWORD_RESET, false);
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)).thenReturn(Optional.of(accessToken));
		when(passwordEncoder.encode(request.newPassword())).thenReturn("newEncodedPassword");
		
		AuthResponse response = authService.resetPassword(request, httpRequest);

        assertThat(response.getMessage()).contains("Password reset successfully");
        assertThat(user.getPassword()).isEqualTo("newEncodedPassword");
		verify(accessTokenRepository).delete(accessToken);
		verify(userRepository).save(user);
		
	}
	
	@Test
    @DisplayName("Email inconnu → BadCredentialsException")
    void resetPassword_unknownEmail_throwsBadCredentials() {
        when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> authService.resetPassword(request, httpRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

	@Test
	@DisplayName("Aucun token trouvé → OTPException")
	void resetPassword_noTokenFound_throwsBadCredentials() {
		User user = buildUser(false);
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> authService.resetPassword(request, httpRequest))
		.isInstanceOf(OTPException.class)
		.hasMessageContaining("Invalid or expired code");
	}
	
	@Test
	@DisplayName("token expiré -> OTPException")
	void resetPassword_expiredToken_throwsOTPException() {
		User user = buildUser(true);
		AccessToken accessToken = buildToken(user, TokenType.PASSWORD_RESET, true);
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)).thenReturn(Optional.of(accessToken));
		
		assertThatThrownBy(() -> authService.resetPassword(request, httpRequest))
		.isInstanceOf(OTPException.class);
		
		verify(accessTokenRepository, never()).delete(accessToken);
		verify(userRepository, never()).save(user);
		
	}
	
	@Test
	@DisplayName("token invalide -> OTPEXCeption")
	void resetPassword_invalidToken_throwsOTPEXCeption() {
		request = new ResetPasswordRequest("exemple@gmail.com", "000000", "newPassword");
		User user = buildUser(true);
		AccessToken accessToken = buildToken(user, TokenType.PASSWORD_RESET, false);
		
		when(userRepository.findByEmailIgnoreCase(request.email())).thenReturn(Optional.of(user));
		when(accessTokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)).thenReturn(Optional.of(accessToken));
		
		assertThatThrownBy(() -> authService.resetPassword(request, httpRequest))
		.isInstanceOf(OTPException.class);
		
		
	}
}
