package com.tecnocampus.LS2.protube_back;

import com.tecnocampus.LS2.protube_back.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        // set a 32+ byte secret required by jjwt HMAC
        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "0123456789abcdef0123456789abcdef");

        Field expField = JwtUtil.class.getDeclaredField("expirationMs");
        expField.setAccessible(true);
        expField.setLong(jwtUtil, 3600000L); // 1 hour
    }

    @Test
    void generateAndValidateToken_validToken_success() {
        UserDetails user = new User("bob", "password", Collections.emptyList());

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertEquals("bob", jwtUtil.extractUsername(token));
        assertFalse(jwtUtil.isTokenExpired(token));
        assertTrue(jwtUtil.validateToken(token, user));
    }

    @Test
    void generateToken_expiredToken_detected() throws Exception {
        // make tokens immediately expired
        Field expField = JwtUtil.class.getDeclaredField("expirationMs");
        expField.setAccessible(true);
        expField.setLong(jwtUtil, -1000L);

        UserDetails user = new User("alice", "pwd", Collections.emptyList());
        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtUtil.isTokenExpired(token));
        assertFalse(jwtUtil.validateToken(token, user));
    }

    @Test
    void extractClaim_returnsIssuedAt() {
        UserDetails user = new User("carol", "pwd", Collections.emptyList());
        String token = jwtUtil.generateToken(user);

        Date issuedAt = jwtUtil.extractClaim(token, Claims::getIssuedAt);
        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new Date(System.currentTimeMillis() + 1000L)));
    }
}
