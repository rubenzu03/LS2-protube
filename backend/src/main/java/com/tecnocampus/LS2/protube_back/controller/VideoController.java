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

    @GetMapping()
    public ResponseEntity<List<VideoDTO>> getVideos() {
        return ResponseEntity.ok().body(videoService.getVideos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideo(@PathVariable Long id) {
        return ResponseEntity.ok().body(videoService.getVideoById(id));
    }

    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(@RequestBody VideoDTO videoDTO) {
        return ResponseEntity.created(videoService.createVideo(videoDTO)).body(videoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VideoDTO> deleteVideo(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.deleteVideo(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> updateVideo(@PathVariable Long id, @RequestBody VideoDTO videoDTO) {
        return ResponseEntity.ok(videoService.updateVideo(id, videoDTO));
    }
}
