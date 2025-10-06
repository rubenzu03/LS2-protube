package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    private Long id;
    private String content;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
}
