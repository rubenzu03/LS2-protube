package com.tecnocampus.LS2.protube_back.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

    @InjectMocks
    IndexController indexController;

    @Test
    void home() {

        assertEquals("index", indexController.home().getViewName());
    }

    @Test
    void logout() {

        assertEquals("logout", indexController.logout().getViewName());
    }
}