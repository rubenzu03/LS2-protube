package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/Category")
public class UserController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok().body(categoryService.getCategories());

    }
}
