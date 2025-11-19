package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.persistence.*;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoImportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoImportService {
    private static final Logger LOG = LoggerFactory.getLogger(VideoImportService.class);

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final ApplicationContext applicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public VideoImportService(VideoRepository videoRepository, 
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            TagRepository tagRepository,
                            CommentRepository commentRepository,
                            ApplicationContext applicationContext) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.applicationContext = applicationContext;
    }

    public void importVideosFromJsonFiles(Path directoryPath) throws IOException {
        LOG.info("Starting video import from directory: {}", directoryPath);

        List<File> jsonFiles;
        try (var stream = Files.walk(directoryPath)) {
            jsonFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::toFile)
                    .toList();
        }

        LOG.info("Found {} JSON files to import", jsonFiles.size());

        int successCount = 0;
        int errorCount = 0;

        for (File jsonFile : jsonFiles) {
            try {
                VideoImportDto videoDTO = objectMapper.readValue(jsonFile, VideoImportDto.class);
                // Get the Spring proxy to enable @Transactional
                VideoImportService proxy = applicationContext.getBean(VideoImportService.class);
                proxy.importVideo(videoDTO, jsonFile.getName());
                successCount++;
                LOG.info("Successfully imported video from file: {}", jsonFile.getName());
            } catch (Exception e) {
                errorCount++;
                LOG.error("Error importing video from file: {}", jsonFile.getName(), e);
            }
        }

        LOG.info("Video import completed: {} successful, {} errors", successCount, errorCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importVideo(VideoImportDto dto, String jsonFileName) {
        // Check if ID is null
        if (dto.id() == null) {
            LOG.warn("Video in file {} has null ID, skipping import", jsonFileName);
            throw new IllegalArgumentException("Video ID cannot be null");
        }

        // Check if video already exists
        if (videoRepository.existsById(dto.id())) {
            LOG.debug("Video with id {} already exists, skipping import", dto.id());
            return;
        }

        // Create or find user
        User user = findOrCreateUser(dto.user());

        // Create video entity
        Video video = new Video();
        video.setId(dto.id());
        video.setTitle(dto.title());
        video.setWidth(dto.width() != null ? dto.width().floatValue() : 1920f);
        video.setHeight(dto.height() != null ? dto.height().floatValue() : 1080f);
        video.setDuration(dto.duration() != null ? dto.duration().floatValue() : 0f);
        video.setUser(user);
        
        // Set filename based on JSON file name (e.g., "0.json" -> "0.mp4")
        String filename = jsonFileName.replace(".json", ".mp4");
        video.setFilename(filename);

        // Handle meta data if present
        if (dto.meta() != null) {
            video.setDescription(dto.meta().description());

            // Add categories
            if (dto.meta().categories() != null && !dto.meta().categories().isEmpty()) {
                List<Category> categories = new ArrayList<>();
                for (String categoryName : dto.meta().categories()) {
                    Category category = findOrCreateCategory(categoryName);
                    categories.add(category);
                }
                video.setCategories(categories);
            }

            // Add tags
            if (dto.meta().tags() != null && !dto.meta().tags().isEmpty()) {
                List<Tag> tags = new ArrayList<>();
                for (String tagName : dto.meta().tags()) {
                    Tag tag = findOrCreateTag(tagName);
                    tags.add(tag);
                }
                video.setTags(tags);
            }
        }

        // Save video first
        videoRepository.save(video);

        // Handle comments if present
        if (dto.meta() != null && dto.meta().comments() != null && !dto.meta().comments().isEmpty()) {
            List<Comment> comments = new ArrayList<>();
            for (VideoImportDto.CommentImportDto commentDto : dto.meta().comments()) {
                Comment comment = new Comment();
                comment.setContent(commentDto.text());
                comment.setVideo(video);
                
                // Find or create comment author
                User commentAuthor = findOrCreateUser(commentDto.author());
                comment.setUser(commentAuthor);
                
                comments.add(comment);
            }
            commentRepository.saveAll(comments);
        }

        LOG.debug("Imported video: {} (ID: {})", video.getTitle(), video.getId());
    }

    private User findOrCreateUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user = userRepository.save(user);
            LOG.debug("Created new user: {}", username);
        }
        return user;
    }

    private Category findOrCreateCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(categoryName);
                    Category saved = categoryRepository.save(category);
                    LOG.debug("Created new category: {}", categoryName);
                    return saved;
                });
    }

    private Tag findOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    Tag saved = tagRepository.save(tag);
                    LOG.debug("Created new tag: {}", tagName);
                    return saved;
                });
    }
}
