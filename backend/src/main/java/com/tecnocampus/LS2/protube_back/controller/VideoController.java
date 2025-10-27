package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    VideoService videoService;

    @Value("${pro_tube.store.dir}")
    private String storeDir;

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
        videoService.createVideo(videoDTO);
        return ResponseEntity.status(201).body(videoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> updateVideo(@PathVariable Long id, @RequestBody VideoDTO videoDTO) {
        videoService.updateVideo(id, videoDTO);
        return ResponseEntity.ok().body(videoDTO);
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long id) {
        VideoDTO video = videoService.getVideoById(id);
        if (video == null || video.filename() == null) {
            return ResponseEntity.notFound().build();
        }

        File videoFile = new File(storeDir, video.filename());
        System.out.println(videoFile.getAbsolutePath());
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(videoFile);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + video.filename() + "\"")
                .body(resource);
    }
}
