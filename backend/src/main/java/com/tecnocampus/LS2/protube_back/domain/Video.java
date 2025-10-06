package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity (name = "videos")
@Getter
@Setter
public class Video {
    @Id
    private Long id;
    private String title;
    private float width;
    private float height;
    private float duration;
    private String user;
    private String description;
    @ManyToMany
    private List<Category> categories;
    private List<String> tags;
}
