package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    VideoService videoService;

    @GetMapping("")
    public ResponseEntity<List<VideoDTO>> getVideos() {
        return ResponseEntity.ok().body(videoService.getVideos());
    }

    @GetMapping("/{id}")
    public ResponseEntity

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestBody VideoDTO videoDTO) {
        videoService.createVideo(videoDTO);
        return ResponseEntity.ok().body("Video uploaded successfully");
    }





}
