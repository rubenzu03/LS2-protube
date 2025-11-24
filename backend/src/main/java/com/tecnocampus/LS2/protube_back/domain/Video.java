package com.tecnocampus.LS2.protube_back.domain;

import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
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
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String filename;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany
    @JoinTable(
        name = "videos_categories",
        joinColumns = @JoinColumn(name = "video_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
    @ManyToMany
    @JoinTable(
        name = "videos_tags",
        joinColumns = @JoinColumn(name = "video_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
    @OneToMany(mappedBy = "video")
    private List<Comment> comments;

    public Video() {}

    public Video(VideoDTO videoDTO){
        this.title = videoDTO.title();
        this.width = videoDTO.width();
        this.height = videoDTO.height();
        this.duration = videoDTO.duration();
        this.filename = videoDTO.filename();
        this.description = videoDTO.description();
    }

    public void updateVideo(VideoDTO videoDTO){
        this.title = videoDTO.title();
        this.width = videoDTO.width();
        this.height = videoDTO.height();
        this.filename = videoDTO.filename();
        this.duration = videoDTO.duration();
        this.description = videoDTO.description();
    }
}
