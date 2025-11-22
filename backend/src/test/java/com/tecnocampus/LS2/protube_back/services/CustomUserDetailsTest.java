package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }


    @Test
    void loadUserByUsername_userFound_returnsUserDetails() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("secret");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDetails ud = customUserDetailsService.loadUserByUsername("testuser");

        assertEquals("testuser", ud.getUsername());
        assertEquals("secret", ud.getPassword());
        assertTrue(ud.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("alice")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("alice"));
    }
}
