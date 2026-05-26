package com.laraim237.restoX.auth.controller;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.AUTH_BASE_URL;
import static com.laraim237.restoX.commons.utils.AuthTestHelpers.json;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.config.security.CustomAccessDeniedHandler;
import com.laraim237.restoX.config.security.Http401UnauthorizedEntryPoint;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.ResetPasswordRequest;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.AuthService;

@SpringBootTest
@DisplayName("POST /reset-password")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ResetPasswordControllerTest {
	 @MockitoBean AuthService authService;
	 @MockitoBean AuditService auditService;
	 @Autowired MockMvc mockMvc;
	
	@Test
    @DisplayName("200 — code valide")
    void resetPassword_valid_returns200() throws Exception {
        ResetPasswordRequest req = new ResetPasswordRequest("john@example.com", "123456", "newPassword123");

        when(authService.resetPassword(any(), any()))
                .thenReturn(AuthResponse.builder().message("Password reset successfully").build());

        mockMvc.perform(post(AUTH_BASE_URL + "/reset-password").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                        .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully"));
    }

    @Test
    @DisplayName("400 — code expiré")
    void resetPassword_expiredCode_returns400() throws Exception {
        ResetPasswordRequest req = new ResetPasswordRequest("john@example.com", "123456", "newPassword123");

        when(authService.resetPassword(any(), any()))
                .thenThrow(new OTPException("Expired code"));

        mockMvc.perform(post(AUTH_BASE_URL + "/reset-password").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isBadRequest());
    }
}
