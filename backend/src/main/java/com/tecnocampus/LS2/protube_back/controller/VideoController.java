package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.dto.ThumbnailDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @GetMapping("/search")
    public ResponseEntity<List<VideoDTO>> searchVideos(@RequestParam("q") String query) {
        return ResponseEntity.ok().body(videoService.getVideosBySearch(query));
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

    @GetMapping("/thumbnail/{id}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Long id) {
        VideoDTO video = videoService.getVideoById(id);
        if (video == null || video.filename() == null) {
            return ResponseEntity.notFound().build();
        }

        String baseName = stripExtension(video.filename());
        File thumb = findThumbnailFile(baseName);
        if (thumb == null || !thumb.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(thumb);
        MediaType contentType = contentTypeFor(thumb.getName());
        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + thumb.getName() + "\"")
                .body(resource);
    }

    @GetMapping("/thumbnails")
    public ResponseEntity<List<ThumbnailDTO>> listThumbnails() {
        List<VideoDTO> videos = videoService.getVideos();
        List<ThumbnailDTO> result = new ArrayList<>();
        for (VideoDTO v : videos) {
            if (v.filename() == null)
                continue;
            String base = stripExtension(v.filename());
            File f = findThumbnailFile(base);
            if (f != null && f.exists()) {
                result.add(new ThumbnailDTO(v.id(), f.getName()));
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload")
    public ResponseEntity<VideoDTO> uploadVideo(@RequestParam MultipartFile file, @RequestParam String metadata) {
        String filename = Objects.requireNonNull(file.getOriginalFilename());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            VideoDTO videoDTO = objectMapper.readValue(metadata, VideoDTO.class);

            File videoFile = new File(storeDir, filename);
            file.transferTo(videoFile);

            VideoDTO videoDTOWithFilename = new VideoDTO(
                    videoDTO.id(),
                    videoDTO.title(),
                    videoDTO.width(),
                    videoDTO.height(),
                    videoDTO.duration(),
                    videoDTO.description(),
                    filename,
                    videoDTO.userId(),
                    videoDTO.categoryId(),
                    videoDTO.tagId(),
                    videoDTO.commentId()
            );

            Video video = new Video(videoDTOWithFilename);
            videoService.videoRepository.save(video);
            return ResponseEntity.status(201).body(videoDTOWithFilename);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    private File findThumbnailFile(String baseName) {
        String[] exts = new String[] { ".webp", ".jpg", ".jpeg", ".png" };
        for (String ext : exts) {
            File f = new File(storeDir, baseName + ext);
            if (f.exists())
                return f;
        }
        return null;
    }

    private String stripExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(0, idx) : filename;
    }

    private MediaType contentTypeFor(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".webp"))
            return MediaType.parseMediaType("image/webp");
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
            return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".png"))
            return MediaType.IMAGE_PNG;
        return MediaType.APPLICATION_OCTET_STREAM;
    }


}
