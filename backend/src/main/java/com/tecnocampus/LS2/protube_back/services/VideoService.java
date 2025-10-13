package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    public VideoRepository videoRepository;

    public VideoService() {
    }

    public List<VideoDTO> getVideos() {
        return videoRepository.findAll().stream().map(this::toDTO).toList();
    }

    public VideoDTO getVideoById(Long id) {
        return videoRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public URI createVideo(VideoDTO videoDTO) {
        Video video = toEntity(videoDTO);
        videoRepository.save(video);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(video.getId())
                .toUri();
    }

    public VideoDTO deleteVideo(Long id) {
        VideoDTO videoDTO = getVideoById(id);
        videoRepository.deleteById(id);
        return videoDTO;
    }

    public VideoDTO updateVideo(Long id, VideoDTO videoDTO) {
        Video video = toEntity(videoDTO);
        video.setId(id);
        videoRepository.save(video);
        return toDTO(video);
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
                video.getCategories() != null && !video.getCategories().isEmpty() ? video.getCategories().get(0).getId() : null,
                video.getTags() != null ? video.getTags().stream().map(tag -> tag.getName()).toList() : null,
                video.getComments() != null && !video.getComments().isEmpty() ? video.getComments().get(0).getId() : null
        );
    }

    private Video toEntity(VideoDTO videoDTO) {
        Video video = new Video();
        video.setId(videoDTO.id());
        video.setTitle(videoDTO.title());
        video.setWidth(videoDTO.width());
        video.setHeight(videoDTO.height());
        video.setDuration(videoDTO.duration());
        video.setDescription(videoDTO.description());
        return video;
    }
}
