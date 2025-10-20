package com.tecnocampus.LS2.protube_back.domain;

import com.tecnocampus.LS2.protube_back.persistence.dto.CategoryDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "categories")
@Getter
@Setter
public class Category {
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String name;

    public Category() {}

    public Category(CategoryDTO categoryDTO) {
        this.id = categoryDTO.id();
        this.name = categoryDTO.name();
    }

    public void updateCategory(CategoryDTO categoryDTO) {
        this.name = categoryDTO.name();
    }

}
