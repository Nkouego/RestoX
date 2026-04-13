package com.laraim237.restoX.modules.auth.controller;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.AUTH_BASE_URL;
import static com.laraim237.restoX.commons.utils.AuthTestHelpers.json;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.ResendCodeRequest;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.AuthService;


@SpringBootTest
@DisplayName("POST /resend-code")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ResendCodeControllerTest {
	 @MockitoBean AuthService authService;
	 @MockitoBean AuditService auditService;
	 @Autowired MockMvc mockMvc;

	@Test
    @DisplayName(" 200 — nouveau code envoyé")
    void resendCode_returns200() throws Exception {
        when(authService.resendCode(any(), any()))
                .thenReturn(AuthResponse.builder().message("If this email exists, a new verification code has been sent.").build());

        mockMvc.perform(post(AUTH_BASE_URL + "/resend-code").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(new ResendCodeRequest("john@example.com"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("400 — compte déjà actif")
    void resendCode_alreadyActive_returns400() throws Exception {
        when(authService.resendCode(any(), any()))
                .thenThrow(new OTPException("Account is already verified"));

        mockMvc.perform(post(AUTH_BASE_URL + "/resend-code").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(new ResendCodeRequest("john@example.com"))))
                .andExpect(status().isBadRequest());
    }

}
