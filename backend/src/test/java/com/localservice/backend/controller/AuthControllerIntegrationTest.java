package com.localservice.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void register_shouldSucceed_withValidData() throws Exception {
        Map<String, String> request = Map.of(
                "name", "Integration Test User",
                "email", "integrationtest@example.com",
                "password", "testpass123",
                "role", "CUSTOMER");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("integrationtest@example.com"));
    }

    @Test
    void register_shouldFail_withInvalidEmail() throws Exception {
        Map<String, String> request = Map.of(
                "name", "Test",
                "email", "not-an-email",
                "password", "testpass123",
                "role", "CUSTOMER");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void login_shouldFail_withWrongPassword() throws Exception {
        // First register a real user
        Map<String, String> registerRequest = Map.of(
                "name", "Login Test User",
                "email", "logintest@example.com",
                "password", "correctpassword",
                "role", "CUSTOMER");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Then try logging in with the wrong password
        Map<String, String> loginRequest = Map.of(
                "email", "logintest@example.com",
                "password", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}