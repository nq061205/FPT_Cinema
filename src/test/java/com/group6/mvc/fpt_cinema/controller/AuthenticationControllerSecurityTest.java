package com.group6.mvc.fpt_cinema.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.group6.mvc.fpt_cinema.dto.request.RegisterRequest;
import com.group6.mvc.fpt_cinema.dto.response.RegisterResponse;
import com.group6.mvc.fpt_cinema.service.TokenRevocationService;
import com.group6.mvc.fpt_cinema.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenRevocationService tokenRevocationService;

    @Test
    void registerDoesNotRequireAuthentication() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(new RegisterResponse(
                        "customer@test.com",
                        "Postman Customer",
                        "0901234567"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Postman Customer",
                                  "email": "customer@test.com",
                                  "phone": "0901234567",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.result.email").value("customer@test.com"));
    }

    @Test
    void logoutRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(1007));
    }

    @Test
    void authenticatedUserCanLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(jwt().jwt(jwt -> jwt.claim("jti", "logout-token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Logout successful"));

        verify(tokenRevocationService).revoke(any());
    }
}
