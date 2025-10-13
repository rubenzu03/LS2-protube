package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.*;
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
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String description;
    @ManyToMany
    private List<Category> categories;
    @ManyToMany
    private List<Tag> tags;
    @OneToMany(mappedBy = "video")
    private List<Comment> comments;
}
