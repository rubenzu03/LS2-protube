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
    @Test
    void deleteCategory_existing_deletesAndReturnsDTO() {
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
        Category c = new Category();
        c.setId(101L);
        c.setName("toDelete");
        Mockito.when(categoryRepository.findById(101L)).thenReturn(Optional.of(c));

        CategoryService service = new CategoryService(categoryRepository);

        var out = service.deleteCategory(101L);
        assertNotNull(out);
        assertEquals(101L, out.id());
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(101L);
    }
    @Test
    void deleteCategory_notFound_returnsNull() {
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
        Mockito.when(categoryRepository.findById(202L)).thenReturn(Optional.empty());

        CategoryService service = new CategoryService(categoryRepository);
        assertNull(service.deleteCategory(202L));
        Mockito.verify(categoryRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void updateCategory_existing_updatesAndReturnsDTO() {
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
        Category existing = new Category();
        existing.setId(303L);
        existing.setName("old");

        Mockito.when(categoryRepository.findById(303L)).thenReturn(Optional.of(existing));
        Mockito.when(categoryRepository.save(ArgumentMatchers.any(Category.class))).thenAnswer(i -> i.getArgument(0));

        CategoryDTO dto = new CategoryDTO(null, "newName");

        CategoryService service = new CategoryService(categoryRepository);
        var out = service.updateCategory(303L, dto);

        assertNotNull(out);
        assertEquals(303L, out.id());
        assertEquals("newName", out.name());
    }

    @Test
    void updateCategory_notFound_returnsNull() {
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
        Mockito.when(categoryRepository.findById(404L)).thenReturn(Optional.empty());

        CategoryService service = new CategoryService(categoryRepository);
        CategoryDTO dto = new CategoryDTO(null, "x");

        assertNull(service.updateCategory(404L, dto));
    }
}

