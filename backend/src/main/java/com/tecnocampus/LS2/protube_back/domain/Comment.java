package com.tecnocampus.LS2.protube_back.domain;

import com.tecnocampus.LS2.protube_back.persistence.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "comments")
@Getter
@Setter
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String content;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    public Comment() {
    }

    public Comment(CommentDTO commentDto) {
        this.content = commentDto.content();
    }
}