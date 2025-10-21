package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.persistence.*;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    public final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, UserRepository userRepository, CategoryRepository categoryRepository, TagRepository tagRepository, CommentRepository commentRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    public List<VideoDTO> getVideos() {
        return videoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public VideoDTO getVideoById(Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return null;
        }
        return toDTO(video);
    }

    public VideoDTO createVideo(VideoDTO videoDTO) {
        Video video = toDomain(videoDTO);
        Video saved = videoRepository.save(video);
        return toDTO(saved);
    }

    public VideoDTO deleteVideo(Long id) {
        VideoDTO videoDTO = getVideoById(id);
        if (videoDTO != null) {
            videoRepository.deleteById(id);
        }
        return videoDTO;
    }

    public VideoDTO updateVideo(Long id, VideoDTO videoDTO) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video != null) {
            video.updateVideo(videoDTO);
            User user = userRepository.findById(videoDTO.userId()).orElse(null);
            Category category = categoryRepository.findById(videoDTO.categoryId()).orElse(null);
            Tag tag = tagRepository.findById(videoDTO.tagId()).orElse(null);
            Comment comment = commentRepository.findById(videoDTO.commentId()).orElse(null);

            if (user != null && category != null && tag != null && comment != null) {
                video.getCategories().add(category);
                video.getTags().add(tag);
                video.getComments().add(comment);
                video.setUser(user);

            }

            Video updated = videoRepository.save(video);
            return toDTO(updated);
        }
        return null;
    }

    private VideoDTO toDTO(Video video) {
        return new VideoDTO(
                video.getId(),
                video.getTitle(),
                video.getWidth(),
                video.getHeight(),
                video.getDuration(),
                video.getDescription(),
                video.getFilename(),
                video.getUser() != null ? video.getUser().getId() : null,
                (video.getCategories() != null && !video.getCategories().isEmpty())
                        ? video.getCategories().get(0).getId()
                        : null,
                (video.getTags() != null && !video.getTags().isEmpty())
                        ? video.getTags().get(0).getId()
                        : null,
                (video.getComments() != null && !video.getComments().isEmpty())
                        ? video.getComments().get(0).getId()
                        : null
        );
    }

    private Video toDomain(VideoDTO videoDTO) {
        Video video = new Video(videoDTO);
        return video;
    }
}
