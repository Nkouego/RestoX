package com.laraim237.restoX.auth.controller;

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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.LoginRequest;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.AuthService;

@SpringBootTest
@DisplayName("POST /login")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerTest {
    @MockitoBean AuthService authService;
    @MockitoBean AuditService auditService;
    @Autowired MockMvc mockMvc;

	@Test
    @DisplayName("credentials corrects")
    void login_valid_returns200() throws Exception {
        LoginRequest req = new LoginRequest("john@example.com", "password123");

        when(authService.login(any(), any()))
                .thenReturn(AuthResponse.builder().token("jwt").refreshToken("refresh").build());

		mockMvc.perform(post(AUTH_BASE_URL + "/login").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"));
    }

    @Test
    @DisplayName("401 — mauvais mot de passe")
    void login_wrongPassword_returns401() throws Exception {
        LoginRequest req = new LoginRequest("john@example.com", "wrongPass");

        when(authService.login(any(), any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post(AUTH_BASE_URL + "/login").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("400 — payload invalide")
    void login_invalidPayload_returns400() throws Exception {
        String bad = """
                {"email":"not-an-email","password":""}
                """;

        mockMvc.perform(post(AUTH_BASE_URL + "/login").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(bad))
                .andExpect(status().isBadRequest());
    }

}
