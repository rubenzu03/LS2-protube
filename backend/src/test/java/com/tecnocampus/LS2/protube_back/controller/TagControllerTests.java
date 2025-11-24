package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.persistence.dto.TagDto;
import com.tecnocampus.LS2.protube_back.security.JwtAuthenticationFilter;
import com.tecnocampus.LS2.protube_back.services.TagService;
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

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TagControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagService tagService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getTags_returnsList() throws Exception {
        TagDto t1 = new TagDto(1L, "tagA");
        TagDto t2 = new TagDto(2L, "tagB");
        given(tagService.getTags()).willReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("tagA"));
    }

    @Test
    void getTagById_returnsTag() throws Exception {
        TagDto t = new TagDto(5L, "special");
        when(tagService.getTagById(5L)).thenReturn(t);

        mockMvc.perform(get("/api/tags/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("special"));
    }

    @Test
    void createTag_returnsCreatedWithBody() throws Exception {
        TagDto input = new TagDto(null, "newtag");
        TagDto created = new TagDto(10L, "newtag");
        when(tagService.createTag(any())).thenReturn(created);

        mockMvc.perform(post("/api/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("newtag"));
    }

    @Test
    void deleteTag_returnsNoContent() throws Exception {
        given(tagService.deleteTag(3L)).willReturn(new TagDto(3L, "toDelete"));

        mockMvc.perform(delete("/api/tags/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateTag_returnsUpdated() throws Exception {
        TagDto input = new TagDto(null, "updated");
        TagDto updated = new TagDto(4L, "updated");
        when(tagService.updateTag(eq(4L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/tags/4")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("updated"));
    }
}
