package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository usersRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @AfterEach
    public void cleanup() throws Exception {
        User user = usersRepository.findByUsername("testuser123");
        if (user != null) {
            usersRepository.delete(user);
        }
    }

    @Test
    public void registerAndLogin_shouldReturnToken() throws Exception {
        var req = new AuthController.AuthRequest("testuser123", "TestPass123");

        // Mock repository behavior for registration
        when(usersRepository.findByUsername("testuser123")).thenReturn(null);
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("encodedPass");
        when(usersRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            // optionally set id if your domain requires it
            return u;
        });

        // Mock authentication and token generation for both register (autologin) and login
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                req.username(), req.password(), Collections.emptyList());
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(ArgumentMatchers.any())).thenReturn(auth);
        when(jwtUtil.generateToken(ArgumentMatchers.any(UserDetails.class))).thenReturn("dummytoken");

        // register
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());

        // login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // verify interactions (optional)
        verify(usersRepository, atLeastOnce()).save(ArgumentMatchers.any(User.class));
        verify(authenticationManager, atLeast(2)).authenticate(ArgumentMatchers.any());
        verify(jwtUtil, atLeast(2)).generateToken(ArgumentMatchers.any(UserDetails.class));
    }
}
