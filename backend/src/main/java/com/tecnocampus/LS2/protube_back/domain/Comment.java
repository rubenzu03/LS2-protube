package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    private Long id;
    private String content;
    @ManyToOne private User user;
}
