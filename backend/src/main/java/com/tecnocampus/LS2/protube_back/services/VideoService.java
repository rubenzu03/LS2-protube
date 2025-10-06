package com.tecnocampus.LS2.protube_back.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    public List<String> getVideos() {

        return List.of("video1", "video2");
    }

    public String getVideoById(String videoId) {
        // Logic to get a video by its ID
        return "videoDetails";
    }

    public void createVideo(String title) {
        // Logic to create a video
    }

    public void deleteVideo(String videoId) {
        // Logic to delete a video
    }

    public void updateVideo(String videoId, String newTitle) {
        // Logic to update a video
    }
}

