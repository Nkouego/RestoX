package com.laraim237.restoX.modules.auth.controller;

import static com.laraim237.restoX.commons.utils.AuthTestHelpers.AUTH_BASE_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.laraim237.restoX.modules.audit.AuditService;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.service.AuthService;

@WebMvcTest(AuthController.class)
@DisplayName("POST /logout")
public class LogoutController {
	 @MockitoBean AuthService authService;
	 @MockitoBean AuditService auditService;
	 @Autowired MockMvc mockMvc;
	
    @Test
    @WithMockUser
    @DisplayName("200 — utilisateur authentifié")
    void logout_authenticated_returns200() throws Exception {
        when(authService.logout(any(), any()))
                .thenReturn(AuthResponse.builder().message("Logged out successfully.").build());

        mockMvc.perform(post(AUTH_BASE_URL + "/logout").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully."));
    }

    @Test
    @DisplayName("❌ 401 — non authentifié")
    void logout_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post(AUTH_BASE_URL + "/logout").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

}
