package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import com.tecnocampus.LS2.protube_back.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getUsers_returnsList() throws Exception {
        UserDTO u1 = new UserDTO(1L, "alice", null, null);
        UserDTO u2 = new UserDTO(2L, "bob", null, null);
        given(userService.getUsers()).willReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    void getUserById_returnsUser() throws Exception {
        UserDTO u = new UserDTO(5L, "charlie", null, null);
        when(userService.getUserById(5L)).thenReturn(u);

        mockMvc.perform(get("/api/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.username").value("charlie"));
    }

    @Test
    void createUser_returnsCreatedWithBody() throws Exception {
        UserDTO input = new UserDTO(null, "newuser", null, null);
        UserDTO created = new UserDTO(10L, "newuser", null, null);
        when(userService.createUser(any())).thenReturn(created);

        mockMvc.perform(post("/api/user")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        given(userService.deleteUser(3L)).willReturn(new UserDTO(3L, "toDelete", null, null));

        mockMvc.perform(delete("/api/user/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUser_returnsUpdated() throws Exception {
        UserDTO input = new UserDTO(null, "updated", null, null);
        UserDTO updated = new UserDTO(4L, "updated", null, null);
        when(userService.updateUser(eq(4L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/user/4")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.username").value("updated"));
    }
}
