package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.persistence.dto.CategoryDTO;
import com.tecnocampus.LS2.protube_back.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok().body(categoryService.getCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.created(categoryService.createCategory(categoryDTO)).body(categoryDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }
}
