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

    //TODO: Check sketchy method, probably there is an easier way to do it
    public List<VideoDTO> getVideos() {
        return videoRepository.findAll()
                .stream()
                .map(v -> new VideoDTO(
                        v.getId(),
                        v.getTitle(),
                        v.getWidth(),
                        v.getHeight(),
                        v.getDuration(),
                        v.getDescription(),
                        v.getUser() != null ? v.getUser().getId() : null,
                        (v.getCategories() != null && !v.getCategories().isEmpty())
                                ? v.getCategories().getFirst().getId()
                                : null,
                        (v.getTags() != null)
                                ? v.getTags().stream().map(Tag::getName).collect(Collectors.toList())
                                : Collections.emptyList(),
                        (v.getComments() != null && !v.getComments().isEmpty())
                                ? v.getComments().getFirst().getId()
                                : null
                ))
                .toList();
    }

    public VideoDTO getVideoById(Long id) {
        Video v = videoRepository.findById(id).orElse(null);
        if (v == null) {
            return null;
        }
        return new VideoDTO(
                v.getId(),
                v.getTitle(),
                v.getWidth(),
                v.getHeight(),
                v.getDuration(),
                v.getDescription(),
                v.getUser() != null ? v.getUser().getId() : null,
                (v.getCategories() != null && !v.getCategories().isEmpty())
                        ? v.getCategories().getFirst().getId()
                        : null,
                (v.getTags() != null)
                        ? v.getTags().stream().map(Tag::getName).collect(Collectors.toList())
                        : Collections.emptyList(),
                (v.getComments() != null && !v.getComments().isEmpty())
                        ? v.getComments().getFirst().getId()
                        : null
        );
    }

    public void createVideo(VideoDTO videoDTO) {
        Video video = new Video(videoDTO);
        videoRepository.save(video);
    }

    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }

    public void updateVideo(Long id, VideoDTO videoDTO) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video != null) {
            video.updateVideo(videoDTO);
            videoRepository.save(video);
        }
    }
}
