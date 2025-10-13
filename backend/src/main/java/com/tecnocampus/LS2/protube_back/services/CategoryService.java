package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Category;
import com.tecnocampus.LS2.protube_back.persistence.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    public CategoryRepository CategoryRepository;

    public CategoryService() {
    }

    public List<Category> getCategories() {
        return CategoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return CategoryRepository.findById(id).orElse(null);
    }

    public void createCategory(Category Category) {
        CategoryRepository.save(Category);
    }

    public void deleteCategory(Long id) {
        CategoryRepository.deleteById(id);
    }

    public void updateCategory(Long id, Category newCategory) {
        newCategory.setId(id);
        CategoryRepository.save(newCategory);
    }
}
