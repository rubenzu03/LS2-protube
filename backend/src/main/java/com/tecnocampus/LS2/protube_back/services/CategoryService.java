package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Category;
import com.tecnocampus.LS2.protube_back.persistence.CategoryRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    public final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = toDomain(categoryDTO);
        Category saved = categoryRepository.save(category);
        return toDTO(saved);
    }

    public CategoryDTO deleteCategory(Long id) {
        CategoryDTO categoryDTO = getCategoryById(id);
        if (categoryDTO != null) {
            categoryRepository.deleteById(id);
        }
        return categoryDTO;
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.updateCategory(categoryDTO);
            Category updated = categoryRepository.save(category);
            return toDTO(updated);
        }
        return null;
    }

    private CategoryDTO toDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

    private Category toDomain(CategoryDTO categoryDTO) {
        return new Category(categoryDTO);
    }
}
