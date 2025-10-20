package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Tag;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    public final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
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
                video.getUser() != null ? video.getUser().getId() : null,
                (video.getCategories() != null && !video.getCategories().isEmpty())
                        ? video.getCategories().get(0).getId()
                        : null,
                (video.getTags() != null)
                        ? video.getTags().stream().map(Tag::getName).collect(Collectors.toList())
                        : Collections.emptyList(),
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
