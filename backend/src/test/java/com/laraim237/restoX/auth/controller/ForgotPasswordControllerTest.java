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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.ForgotPasswordRequest;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("POST /forgot-password")
public class ForgotPasswordControllerTest {
	 @MockitoBean AuthService authService;
	 @MockitoBean AuditService auditService;
	 @Autowired MockMvc mockMvc;


    @Test
    @DisplayName("email existant ou non (réponse générique)")
    void forgotPassword_returns200() throws Exception {
        when(authService.forgotPassword(any(), any()))
                .thenReturn(AuthResponse.builder().message("If this email exists...").build());

        mockMvc.perform(post(AUTH_BASE_URL + "/forgot-password").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(new ForgotPasswordRequest("john@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("400 — email invalide")
    void forgotPassword_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post(AUTH_BASE_URL + "/forgot-password").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\":\"not-valid\"}"))
                .andExpect(status().isBadRequest());
    }

}
