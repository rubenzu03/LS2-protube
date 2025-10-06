package com.tecnocampus.LS2.protube_back.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    public List<String> getCategories() {
        return List.of("category1", "category2");
    }

    public String getCategoryById(String categoryId) {
        // Logic to get a category by its ID
        return "categoryDetails";
    }

    public void createCategory(String name) {
        // Logic to create a category
    }

    public void deleteCategory(String categoryId) {
        // Logic to delete a category
    }

    public void updateCategory(String categoryId, String newName) {
        // Logic to update a category
    }
}
