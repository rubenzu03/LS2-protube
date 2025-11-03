package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.persistence.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.security.JwtAuthenticationFilter;
import com.tecnocampus.LS2.protube_back.services.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getComments_returnsList() throws Exception {
        CommentDTO c1 = new CommentDTO(1L, "hello", 11L, 21L);
        CommentDTO c2 = new CommentDTO(2L, "world", 12L, 22L);
        given(commentService.getComments()).willReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("hello"));
    }

    @Test
    void getCommentById_returnsComment() throws Exception {
        CommentDTO c = new CommentDTO(5L, "a comment", 2L, 3L);
        when(commentService.getCommentById(5L)).thenReturn(c);

        mockMvc.perform(get("/api/comments/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.content").value("a comment"))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.videoId").value(3));
    }

    @Test
    void createComment_returnsCreatedWithBody() throws Exception {
        CommentDTO input = new CommentDTO(null, "new comment", 7L, 8L);
        CommentDTO created = new CommentDTO(10L, "new comment", 7L, 8L);
        when(commentService.createComment(any())).thenReturn(created);

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.content").value("new comment"))
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.videoId").value(8));
    }

    @Test
    void deleteComment_returnsNoContent() throws Exception {
        given(commentService.deleteComment(3L)).willReturn(new CommentDTO(3L, "toDelete", 1L, 1L));

        mockMvc.perform(delete("/api/comments/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateComment_returnsUpdated() throws Exception {
        CommentDTO input = new CommentDTO(null, "updated", 5L, 6L);
        CommentDTO updated = new CommentDTO(4L, "updated", 5L, 6L);
        when(commentService.updateComment(eq(4L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/comments/4")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.content").value("updated"))
                .andExpect(jsonPath("$.userId").value(5))
                .andExpect(jsonPath("$.videoId").value(6));
    }
}
