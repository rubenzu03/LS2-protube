package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.persistence.dto.CategoryDTO;
import com.tecnocampus.LS2.protube_back.security.JwtAuthenticationFilter;
import com.tecnocampus.LS2.protube_back.services.CategoryService;
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

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getCategories_returnsList() throws Exception {
        CategoryDTO c1 = new CategoryDTO(1L, "catA");
        CategoryDTO c2 = new CategoryDTO(2L, "catB");
        given(categoryService.getCategories()).willReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("catA"));
    }

    @Test
    void getCategoryById_returnsCategory() throws Exception {
        CategoryDTO c = new CategoryDTO(5L, "specialCat");
        when(categoryService.getCategoryById(5L)).thenReturn(c);

        mockMvc.perform(get("/api/category/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("specialCat"));
    }

    @Test
    void createCategory_returnsCreatedWithBody() throws Exception {
        CategoryDTO input = new CategoryDTO(null, "newcat");
        CategoryDTO serviceReturn = new CategoryDTO(10L, "newcat");
        when(categoryService.createCategory(any())).thenReturn(serviceReturn);

        mockMvc.perform(post("/api/category")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("newcat"));
    }

    @Test
    void deleteCategory_returnsNoContent() throws Exception {
        given(categoryService.deleteCategory(3L)).willReturn(new CategoryDTO(3L, "toDelete"));

        mockMvc.perform(delete("/api/category/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateCategory_returnsUpdatedBody() throws Exception {
        CategoryDTO input = new CategoryDTO(null, "updated");
        CategoryDTO serviceReturn = new CategoryDTO(4L, "updated");
        when(categoryService.updateCategory(eq(4L), any())).thenReturn(serviceReturn);

        mockMvc.perform(put("/api/category/4")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"));
    }
}
