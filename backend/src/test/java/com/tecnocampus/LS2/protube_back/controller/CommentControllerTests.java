package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
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
import static org.junit.jupiter.api.Assertions.*;

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
        CommentDTO c1 = new CommentDTO(1L, "hello", 11L, 21L,"test");
        CommentDTO c2 = new CommentDTO(2L, "world", 12L, 22L,"test2");

        assertEquals(1L, c1.id());
        assertEquals("hello", c1.content());
        assertNotEquals(c1, c2);
        assertNotNull(c1.toString());

        given(commentService.getComments()).willReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("hello"));
    }

    @Test
    void getCommentById_returnsComment() throws Exception {
        CommentDTO c = new CommentDTO(5L, "a comment", 2L, 3L,"testUser");

        assertEquals(5L, c.id());
        assertEquals("testUser", c.username());
        assertTrue(c.toString().contains("a comment"));
        int h = c.hashCode();
        assertTrue(h != 0 || c.equals(new CommentDTO(5L, "a comment", 2L, 3L, "testUser")));

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
        CommentDTO input = new CommentDTO(null, "new comment", 7L, 8L,"test");
        CommentDTO created = new CommentDTO(10L, "new comment", 7L, 8L,"test");
        assertNull(input.id());
        assertEquals("new comment", input.content());
        assertNotNull(created.toString());

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
        CommentDTO returned = new CommentDTO(3L, "toDelete", 1L, 1L,"test");
        assertEquals(3L, returned.id());
        assertEquals("toDelete", returned.content());
        assertNotNull(returned.toString());

        given(commentService.deleteComment(3L)).willReturn(returned);

        mockMvc.perform(delete("/api/comments/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateComment_returnsUpdated() throws Exception {
        CommentDTO input = new CommentDTO(null, "updated", 5L, 6L,"test");
        CommentDTO updated = new CommentDTO(4L, "updated", 5L, 6L,"test");

        assertEquals("updated", input.content());
        assertEquals(4L, updated.id());
        assertTrue(updated.toString().contains("updated"));

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
    @Test
    void updateComment_changesOnlyContent_notUserOrVideo() {
        Comment c = new Comment();
        User u = new User(7L, "u7");
        Video v = new Video(); v.setId(9L);
        c.setUser(u);
        c.setVideo(v);
        c.setContent("old");

        CommentDTO dto = new CommentDTO(null, "new content", null, null, null);
        c.updateComment(dto);

        assertEquals("new content", c.getContent());
        assertSame(u, c.getUser(), "updateComment should not change user reference");
        assertSame(v, c.getVideo(), "updateComment should not change video reference");
    }

    @Test
    void updateUser_setsAndClearsReference() {
        Comment c = new Comment();
        User u = new User(5L, "alice");

        c.updateUser(u);
        assertSame(u, c.getUser());
        assertEquals(5L, c.getUser().getId());

        c.updateUser(null);
        assertNull(c.getUser(), "updateUser(null) should clear the user reference");
    }

}
