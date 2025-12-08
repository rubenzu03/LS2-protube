package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    VideoService videoService;

    @Autowired
    UserRepository userRepository;

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
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file provided");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                return ResponseEntity.badRequest().body("Invalid filename");
            }

            // Get the authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : null;
            User currentUser = username != null ? userRepository.findByUsername(username) : null;
            Long userId = currentUser != null ? currentUser.getId() : null;
            String extension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID() + extension;
            String baseName = stripExtension(uniqueFilename);

            // Save the file to the store directory
            Path storePath = Path.of(storeDir);
            if (!Files.exists(storePath)) {
                Files.createDirectories(storePath);
            }
            Path targetPath = storePath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Try to extract video metadata using JAVE2; use defaults if it fails
            float duration = 0;
            float width = 0;
            float height = 0;
            try {
                File videoFile = targetPath.toFile();
                MultimediaObject multimediaObject = new MultimediaObject(videoFile);
                MultimediaInfo info = multimediaObject.getInfo();
                duration = info.getDuration() / 1000f;
                if (info.getVideo() != null) {
                    VideoSize size = info.getVideo().getSize();
                    if (size != null) {
                        width = size.getWidth();
                        height = size.getHeight();
                    }
                }
            } catch (Exception ignored) {
                // JAVE2/FFmpeg not available or failed; continue with zero values
            }

            // If title was not provided, use the original filename (without extension)
            if (title == null || title.isBlank()) {
                title = stripExtension(originalFilename);
            }

            VideoDTO videoDTO = new VideoDTO(
                    null, // id - will be generated
                    title,
                    width,
                    height,
                    duration,
                    description,
                    uniqueFilename,
                    userId, // userId from authenticated user
                    null, // categoryId
                    null, // tagId
                    null // commentId
            );

            VideoDTO savedVideo = videoService.createVideo(videoDTO);

            // Generate thumbnail and JSON metadata (best effort, don't fail upload if these
            // fail)
            generateThumbnail(targetPath.toString(), storePath.resolve(baseName + ".webp").toString());
            generateJsonMetadata(storePath.resolve(baseName + ".json").toString(), savedVideo, title, description,
                    username);

            return ResponseEntity.status(201).body(savedVideo);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }

    private void generateThumbnail(String videoPath, String thumbnailPath) {
        try {
            // Try to generate thumbnail using FFmpeg (extract frame at 1 second)
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-y", "-i", videoPath,
                    "-ss", "00:00:01", "-vframes", "1",
                    "-vf", "scale=320:-1",
                    thumbnailPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();
        } catch (Exception ignored) {
            // FFmpeg not available or failed; thumbnail won't be created
        }
    }

    private void generateJsonMetadata(String jsonPath, VideoDTO video, String title, String description,
            String username) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            root.put("id", video.id());
            root.put("width", (int) video.width());
            root.put("height", (int) video.height());
            root.put("duration", video.duration());
            root.put("title", title);
            root.put("user", username != null ? username : "anonymous");

            ObjectNode meta = mapper.createObjectNode();
            meta.put("description", description != null ? description : "");
            meta.set("categories", mapper.createArrayNode());
            meta.set("tags", mapper.createArrayNode());
            meta.set("comments", mapper.createArrayNode());
            root.set("meta", meta);

            try (FileWriter writer = new FileWriter(jsonPath)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(writer, root);
            }
        } catch (Exception ignored) {
            // JSON generation failed; continue without it
        }
    }

    private String getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx) : "";
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
