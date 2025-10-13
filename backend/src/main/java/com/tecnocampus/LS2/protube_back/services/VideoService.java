package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    @Autowired
    public VideoRepository videoRepository;

    public VideoService() {
    }

    public List<Video> getVideos() {

        return videoRepository.findAll();
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id).orElse(null);
    }

    public void createVideo(Video video) {
        videoRepository.save(video);
    }

    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }

    public void updateVideo(Long id, Video newVideo) {
        newVideo.setId(id);
        videoRepository.save(newVideo);
    }
}
