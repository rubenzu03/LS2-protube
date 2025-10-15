package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Category;
import com.tecnocampus.LS2.protube_back.persistence.CategoryRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class CategoryService {

    public final CategoryRepository CategoryRepository;

    public CategoryService(CategoryRepository CategoryRepository) {
        this.CategoryRepository = CategoryRepository;
    }

    public List<CategoryDTO> getCategories() {
        return CategoryRepository.findAll().stream().map(this::toDTO).toList();
    }

    public CategoryDTO getCategoryById(Long id) {
        return CategoryRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public URI createCategory(CategoryDTO categoryDTO) {
        Category category = toEntity(categoryDTO);
        CategoryRepository.save(category);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(category.getId())
                .toUri();
    }

    public CategoryDTO deleteCategory(Long id) {
        CategoryDTO categoryDTO = getCategoryById(id);
        CategoryRepository.deleteById(id);
        return categoryDTO;
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = toEntity(categoryDTO);
        category.setId(id);
        CategoryRepository.save(category);
        return toDTO(category);
    }

    private CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName()
        );
    }

    private Category toEntity(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setId(categoryDTO.id());
        category.setName(categoryDTO.name());
        return category;
    }
}
