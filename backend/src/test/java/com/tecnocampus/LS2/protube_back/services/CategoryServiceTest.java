package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Category;
import com.tecnocampus.LS2.protube_back.persistence.CategoryRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.CategoryDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    @Test
    void getCategories_emptyList_returnsEmpty() {
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
        Mockito.when(categoryRepository.findAll()).thenReturn(List.of());

        CategoryService service = new CategoryService(categoryRepository);

        assertTrue(service.getCategories().isEmpty());
    }

    @Test
    void getCategoryById_notFound_returnsNull() {
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        CategoryService service = new CategoryService(categoryRepository);

        assertNull(service.getCategoryById(1L));
    }

    @Test
    void createCategory_savesAndReturnsDTO() {
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
        CategoryDTO dto = new CategoryDTO(null, "cat");
        Category saved = new Category(dto);
        saved.setId(33L);

        Mockito.when(categoryRepository.save(ArgumentMatchers.any(Category.class))).thenReturn(saved);

        CategoryService service = new CategoryService(categoryRepository);

        CategoryDTO result = service.createCategory(dto);
        assertNotNull(result);
        assertEquals(33L, result.id());
        assertEquals("cat", result.name());
    }
}

