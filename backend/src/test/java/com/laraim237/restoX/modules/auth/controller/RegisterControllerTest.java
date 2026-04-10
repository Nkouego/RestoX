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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.laraim237.restoX.common.Exception.AccountAlreadyExistsException;
import com.laraim237.restoX.modules.audit.AuditService;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.modules.auth.service.AuthService;

import tools.jackson.databind.ObjectMapper;


@SpringBootTest
@DisplayName("POST /register")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RegisterControllerTest {
	@Autowired MockMvc mockMvc;
	@Autowired ObjectMapper objectMapper;
    @MockitoBean AuthService authService;
    @MockitoBean AuditService auditService;
  
    
   
	
	@Test
    @DisplayName("✅ 201 — payload valide")
    void register_valid_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest("John", "Doe", "john@example.com", "password123", "Mon Resto");

        when(authService.register(any(), any()))
                .thenReturn(AuthResponse.builder().message("Account created successfully.").build());

        mockMvc.perform(post(AUTH_BASE_URL+ "/register").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Account created successfully."));
    }

    @Test
    @WithMockUser
    @DisplayName("409 — email déjà existant")
    void register_duplicateEmail_returns409() throws Exception {
        RegisterRequest req = new RegisterRequest("John", "Doe", "john@example.com", "password123", "Mon Resto");

        when(authService.register(any(), any()))
                .thenThrow(new AccountAlreadyExistsException("Account already exists"));

        mockMvc.perform(post(AUTH_BASE_URL + "/register").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("400 — champs manquants / invalides")
    void register_invalidPayload_returns400() throws Exception {
        // email invalide, password trop court, firstName vide
        String badPayload = """
                {"firstName":"","lastName":"Doe","email":"not-an-email","password":"123","restaurantName":"R"}
                """;

        mockMvc.perform(post(AUTH_BASE_URL + "/register").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }
}
