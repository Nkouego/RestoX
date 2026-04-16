package com.laraim237.restoX.modules.auth.controller;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.AUTH_BASE_URL;
import static com.laraim237.restoX.commons.utils.AuthTestHelpers.json;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import com.laraim237.restoX.commons.utils.AuthTestHelpers;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.VerifyEmailRequest;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.AuthService;

@DisplayName("POST /verify-email")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class VerifyEmailControllerTest {

	@MockitoBean AuthService authService;
	@MockitoBean AuditService auditService;
	@Autowired MockMvc mockMvc;

	@Test
    @DisplayName("200 — code valide")
    void verifyEmail_valid_returns200() throws Exception {
        VerifyEmailRequest req = new VerifyEmailRequest("john@example.com", "123456");

        when(authService.verifyEmail(any(), any()))
                .thenReturn(AuthResponse.builder().token("jwt").refreshToken("refresh").build());

        mockMvc.perform(post(AUTH_BASE_URL + "/verify-email").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    @DisplayName("400 — code expiré")
    void verifyEmail_expiredCode_returns400() throws Exception {
        VerifyEmailRequest req = new VerifyEmailRequest("john@example.com", "123456");

        when(authService.verifyEmail(any(), any()))
                .thenThrow(new OTPException("Expired code"));

        mockMvc.perform(post(AUTH_BASE_URL + "/verify-email").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("400 — code invalide")
    void verifyEmail_invalidCode_returns400() throws Exception {
        VerifyEmailRequest req = new VerifyEmailRequest("john@example.com", "000000");

        when(authService.verifyEmail(any(), any()))
                .thenThrow(new OTPException("Invalid code"));

        mockMvc.perform(post(AUTH_BASE_URL + "/verify-email").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("400 — payload invalide (code != 6 chars)")
    void verifyEmail_invalidPayload_returns400() throws Exception {
        String bad = """
                {"email":"john@example.com","code":"12"}
                """;

        mockMvc.perform(post(AUTH_BASE_URL + "/verify-email").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(bad))
                .andExpect(status().isBadRequest());
    }

}
