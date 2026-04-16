package com.laraim237.restoX.modules.auth.controller;

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

import com.laraim237.restoX.common.Exception.RefreshTokenException;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.RefreshTokenRequest;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.AuthService;

@SpringBootTest
@DisplayName("POST /refresh-token")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RefreshTokenController {
	 @MockitoBean AuthService authService;
	 @MockitoBean AuditService auditService;
	 @Autowired MockMvc mockMvc;

	
	 @Test
     @DisplayName("200 — token valide")
     void refreshToken_valid_returns200() throws Exception {
         when(authService.refreshToken(any(), any()))
                 .thenReturn(AuthResponse.builder().token("new-jwt").refreshToken("new-refresh").build());

         mockMvc.perform(post(AUTH_BASE_URL + "/refresh-token").with(csrf())
                         .contentType(APPLICATION_JSON)
                         .content(json(new RefreshTokenRequest("valid-refresh"))))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.token").value("new-jwt"));
     }

     @Test
     @DisplayName("400 — token révoqué/expiré")
     void refreshToken_invalid_returns400() throws Exception {
         when(authService.refreshToken(any(), any()))
                 .thenThrow(new RefreshTokenException("Token revoked"));

         mockMvc.perform(post(AUTH_BASE_URL + "/refresh-token").with(csrf())
                         .contentType(APPLICATION_JSON)
                         .content(json(new RefreshTokenRequest("bad-token"))))
         				.andDo(print())
                 .andExpect(status().isUnauthorized());
     }

     @Test
     @DisplayName("400 — refreshToken manquant")
     void refreshToken_missingField_returns400() throws Exception {
         mockMvc.perform(post(AUTH_BASE_URL + "/refresh-token").with(csrf())
                         .contentType(APPLICATION_JSON)
                         .content("{}"))
                 .andExpect(status().isBadRequest());
     }
 }


