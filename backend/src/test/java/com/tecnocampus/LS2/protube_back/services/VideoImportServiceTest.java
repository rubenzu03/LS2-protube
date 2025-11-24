package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.persistence.*;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoImportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoImportServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private VideoImportService videoImportService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Setup spy for self-referencing calls (lenient to avoid UnnecessaryStubbingException)
        lenient().when(applicationContext.getBean(VideoImportService.class)).thenReturn(videoImportService);
    }

    @Test
    void testImportVideo_Success_WithAllMetadata() {
        // Given
        VideoImportDto.MetaDto meta = new VideoImportDto.MetaDto(
                "Test Description",
                List.of("Music", "Entertainment"),
                List.of("pop", "music", "fun"),
                List.of(
                        new VideoImportDto.CommentImportDto("Great video!", "Commenter1"),
                        new VideoImportDto.CommentImportDto("Love it!", "Commenter2")
                )
        );

        VideoImportDto dto = new VideoImportDto(
                0L,
                1920,
                1080,
                27.0,
                "Bruno Mars - 24K Magic (Official Music Video)",
                "Bruno Mars",
                meta
        );

        User user = new User();
        user.setId(1L);
        user.setUsername("Bruno Mars");

        User commenter1 = new User();
        commenter1.setId(2L);
        commenter1.setUsername("Commenter1");

        User commenter2 = new User();
        commenter2.setId(3L);
        commenter2.setUsername("Commenter2");

        Category music = new Category();
        music.setId(1L);
        music.setName("Music");

        Category entertainment = new Category();
        entertainment.setId(2L);
        entertainment.setName("Entertainment");

        Tag popTag = new Tag();
        popTag.setId(1L);
        popTag.setName("pop");

        Tag musicTag = new Tag();
        musicTag.setId(2L);
        musicTag.setName("music");

        Tag funTag = new Tag();
        funTag.setId(3L);
        funTag.setName("fun");

        when(videoRepository.existsById(0L)).thenReturn(false);
        when(userRepository.findByUsername("Bruno Mars")).thenReturn(user);
        when(userRepository.findByUsername("Commenter1")).thenReturn(commenter1);
        when(userRepository.findByUsername("Commenter2")).thenReturn(commenter2);
        when(categoryRepository.findByName("Music")).thenReturn(Optional.of(music));
        when(categoryRepository.findByName("Entertainment")).thenReturn(Optional.of(entertainment));
        when(tagRepository.findByName("pop")).thenReturn(Optional.of(popTag));
        when(tagRepository.findByName("music")).thenReturn(Optional.of(musicTag));
        when(tagRepository.findByName("fun")).thenReturn(Optional.of(funTag));

        // When
        videoImportService.importVideo(dto, "0.json");

        // Then
        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository).save(videoCaptor.capture());

        Video savedVideo = videoCaptor.getValue();
        assertEquals(0L, savedVideo.getId());
        assertEquals("Bruno Mars - 24K Magic (Official Music Video)", savedVideo.getTitle());
        assertEquals(1920f, savedVideo.getWidth());
        assertEquals(1080f, savedVideo.getHeight());
        assertEquals(27.0f, savedVideo.getDuration());
        assertEquals("0.mp4", savedVideo.getFilename());
        assertEquals("Test Description", savedVideo.getDescription());
        assertEquals(user, savedVideo.getUser());

        // Verify categories
        assertNotNull(savedVideo.getCategories());
        assertEquals(2, savedVideo.getCategories().size());
        assertTrue(savedVideo.getCategories().contains(music));
        assertTrue(savedVideo.getCategories().contains(entertainment));

        // Verify tags
        assertNotNull(savedVideo.getTags());
        assertEquals(3, savedVideo.getTags().size());
        assertTrue(savedVideo.getTags().contains(popTag));
        assertTrue(savedVideo.getTags().contains(musicTag));
        assertTrue(savedVideo.getTags().contains(funTag));

        // Verify comments
        ArgumentCaptor<List<Comment>> commentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(commentRepository).saveAll(commentsCaptor.capture());

        List<Comment> savedComments = commentsCaptor.getValue();
        assertEquals(2, savedComments.size());
        assertEquals("Great video!", savedComments.get(0).getContent());
        assertEquals(commenter1, savedComments.get(0).getUser());
        assertEquals(savedVideo, savedComments.get(0).getVideo());
        assertEquals("Love it!", savedComments.get(1).getContent());
        assertEquals(commenter2, savedComments.get(1).getUser());
        assertEquals(savedVideo, savedComments.get(1).getVideo());
    }

    @Test
    void testImportVideo_NullId_ThrowsException() {
        // Given
        VideoImportDto dto = new VideoImportDto(
                null,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                null
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            videoImportService.importVideo(dto, "test.json");
        });

        assertEquals("Video ID cannot be null", exception.getMessage());
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void testImportVideo_VideoAlreadyExists_SkipsImport() {
        // Given
        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                null
        );

        when(videoRepository.existsById(1L)).thenReturn(true);

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        verify(videoRepository, never()).save(any(Video.class));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void testImportVideo_CreatesNewUser_WhenNotExists() {
        // Given
        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "NewUser",
                null
        );

        User newUser = new User();
        newUser.setId(1L);
        newUser.setUsername("NewUser");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("NewUser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("NewUser", savedUser.getUsername());
    }

    @Test
    void testImportVideo_UsesExistingUser_WhenExists() {
        // Given
        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "ExistingUser",
                null
        );

        User existingUser = new User();
        existingUser.setId(5L);
        existingUser.setUsername("ExistingUser");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("ExistingUser")).thenReturn(existingUser);

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        verify(userRepository, never()).save(any(User.class));

        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository).save(videoCaptor.capture());
        assertEquals(existingUser, videoCaptor.getValue().getUser());
    }

    @Test
    void testImportVideo_CreatesNewCategory_WhenNotExists() {
        // Given
        VideoImportDto.MetaDto meta = new VideoImportDto.MetaDto(
                "Description",
                List.of("NewCategory"),
                List.of(),
                List.of()
        );

        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                meta
        );

        User user = new User();
        user.setUsername("TestUser");

        Category newCategory = new Category();
        newCategory.setId(1L);
        newCategory.setName("NewCategory");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(categoryRepository.findByName("NewCategory")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());

        Category savedCategory = categoryCaptor.getValue();
        assertEquals("NewCategory", savedCategory.getName());
    }

    @Test
    void testImportVideo_CreatesNewTag_WhenNotExists() {
        // Given
        VideoImportDto.MetaDto meta = new VideoImportDto.MetaDto(
                "Description",
                List.of(),
                List.of("newtag"),
                List.of()
        );

        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                meta
        );

        User user = new User();
        user.setUsername("TestUser");

        Tag newTag = new Tag();
        newTag.setId(1L);
        newTag.setName("newtag");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(tagRepository.findByName("newtag")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(newTag);

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(tagCaptor.capture());

        Tag savedTag = tagCaptor.getValue();
        assertEquals("newtag", savedTag.getName());
    }

    @Test
    void testImportVideo_DefaultDimensions_WhenNull() {
        // Given
        VideoImportDto dto = new VideoImportDto(
                1L,
                null,  // null width
                null,  // null height
                null,  // null duration
                "Test Video",
                "TestUser",
                null
        );

        User user = new User();
        user.setUsername("TestUser");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository).save(videoCaptor.capture());

        Video savedVideo = videoCaptor.getValue();
        assertEquals(1920f, savedVideo.getWidth());
        assertEquals(1080f, savedVideo.getHeight());
        assertEquals(0f, savedVideo.getDuration());
    }

    @Test
    void testImportVideo_GeneratesFilenameFromJsonName() {
        // Given
        VideoImportDto dto = new VideoImportDto(
                42L,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                null
        );

        User user = new User();
        user.setUsername("TestUser");

        when(videoRepository.existsById(42L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);

        // When
        videoImportService.importVideo(dto, "42.json");

        // Then
        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository).save(videoCaptor.capture());

        Video savedVideo = videoCaptor.getValue();
        assertEquals("42.mp4", savedVideo.getFilename());
    }

    @Test
    void testImportVideo_NoMeta_DoesNotSetDescriptionOrRelations() {
        // Given
        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                null  // No meta
        );

        User user = new User();
        user.setUsername("TestUser");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository).save(videoCaptor.capture());

        Video savedVideo = videoCaptor.getValue();
        assertNull(savedVideo.getDescription());
        assertNull(savedVideo.getCategories());
        assertNull(savedVideo.getTags());

        verify(commentRepository, never()).saveAll(anyList());
        verify(categoryRepository, never()).findByName(anyString());
        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    void testImportVideo_EmptyCommentsInMeta_DoesNotSaveComments() {
        // Given
        VideoImportDto.MetaDto meta = new VideoImportDto.MetaDto(
                "Description",
                List.of(),
                List.of(),
                List.of()  // Empty comments
        );

        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                meta
        );

        User user = new User();
        user.setUsername("TestUser");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        verify(commentRepository, never()).saveAll(anyList());
    }

    @Test
    void testImportVideosFromJsonFiles_ProcessesAllJsonFiles() throws IOException {
        // Given
        String json1 = """
                {
                  "id": 1,
                  "width": 1920,
                  "height": 1080,
                  "duration": 30.0,
                  "title": "Video 1",
                  "user": "User1"
                }
                """;

        String json2 = """
                {
                  "id": 2,
                  "width": 1920,
                  "height": 1080,
                  "duration": 25.0,
                  "title": "Video 2",
                  "user": "User2"
                }
                """;

        Path jsonFile1 = tempDir.resolve("1.json");
        Path jsonFile2 = tempDir.resolve("2.json");
        Files.writeString(jsonFile1, json1);
        Files.writeString(jsonFile2, json2);

        User user1 = new User();
        user1.setUsername("User1");
        User user2 = new User();
        user2.setUsername("User2");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(videoRepository.existsById(2L)).thenReturn(false);
        when(userRepository.findByUsername("User1")).thenReturn(user1);
        when(userRepository.findByUsername("User2")).thenReturn(user2);

        // When
        videoImportService.importVideosFromJsonFiles(tempDir);

        // Then
        verify(videoRepository, times(2)).save(any(Video.class));
    }

    @Test
    void testImportVideosFromJsonFiles_InvalidJson_ContinuesWithOtherFiles() throws IOException {
        // Given
        String invalidJson = "{ invalid json content }";
        String validJson = """
                {
                  "id": 2,
                  "width": 1920,
                  "height": 1080,
                  "duration": 30.0,
                  "title": "Valid Video",
                  "user": "TestUser"
                }
                """;

        Path invalidFile = tempDir.resolve("invalid.json");
        Path validFile = tempDir.resolve("2.json");
        Files.writeString(invalidFile, invalidJson);
        Files.writeString(validFile, validJson);

        User user = new User();
        user.setUsername("TestUser");

        when(videoRepository.existsById(2L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);

        // When
        videoImportService.importVideosFromJsonFiles(tempDir);

        // Then - Should still process the valid file
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    void testImportVideosFromJsonFiles_NullIdInJson_ContinuesWithOtherFiles() throws IOException {
        // Given
        String nullIdJson = """
                {
                  "id": null,
                  "width": 1920,
                  "height": 1080,
                  "duration": 30.0,
                  "title": "Null ID Video",
                  "user": "User1"
                }
                """;

        String validJson = """
                {
                  "id": 2,
                  "width": 1920,
                  "height": 1080,
                  "duration": 30.0,
                  "title": "Valid Video",
                  "user": "User2"
                }
                """;

        Path nullIdFile = tempDir.resolve("null.json");
        Path validFile = tempDir.resolve("2.json");
        Files.writeString(nullIdFile, nullIdJson);
        Files.writeString(validFile, validJson);

        User user2 = new User();
        user2.setUsername("User2");

        when(videoRepository.existsById(2L)).thenReturn(false);
        when(userRepository.findByUsername("User2")).thenReturn(user2);

        // When
        videoImportService.importVideosFromJsonFiles(tempDir);

        // Then - Should only process the valid file
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    void testImportVideosFromJsonFiles_EmptyDirectory_ProcessesZeroFiles() throws IOException {
        // Given - empty directory (tempDir has no files)

        // When
        videoImportService.importVideosFromJsonFiles(tempDir);

        // Then
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void testImportVideosFromJsonFiles_OnlyNonJsonFiles_ProcessesZeroFiles() throws IOException {
        // Given
        Path txtFile = tempDir.resolve("readme.txt");
        Path mp4File = tempDir.resolve("video.mp4");
        Files.writeString(txtFile, "Some text");
        Files.writeString(mp4File, "video content");

        // When
        videoImportService.importVideosFromJsonFiles(tempDir);

        // Then
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void testImportVideo_MultipleCategories_AllAdded() {
        // Given
        VideoImportDto.MetaDto meta = new VideoImportDto.MetaDto(
                "Description",
                List.of("Music", "Entertainment", "Pop"),
                List.of(),
                List.of()
        );

        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                meta
        );

        User user = new User();
        user.setUsername("TestUser");

        Category music = new Category();
        music.setName("Music");
        Category entertainment = new Category();
        entertainment.setName("Entertainment");
        Category pop = new Category();
        pop.setName("Pop");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(categoryRepository.findByName("Music")).thenReturn(Optional.of(music));
        when(categoryRepository.findByName("Entertainment")).thenReturn(Optional.of(entertainment));
        when(categoryRepository.findByName("Pop")).thenReturn(Optional.of(pop));

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository).save(videoCaptor.capture());

        Video savedVideo = videoCaptor.getValue();
        assertEquals(3, savedVideo.getCategories().size());
        assertTrue(savedVideo.getCategories().contains(music));
        assertTrue(savedVideo.getCategories().contains(entertainment));
        assertTrue(savedVideo.getCategories().contains(pop));
    }

    @Test
    void testImportVideo_MultipleTags_AllAdded() {
        // Given
        VideoImportDto.MetaDto meta = new VideoImportDto.MetaDto(
                "Description",
                List.of(),
                List.of("tag1", "tag2", "tag3", "tag4"),
                List.of()
        );

        VideoImportDto dto = new VideoImportDto(
                1L,
                1920,
                1080,
                30.0,
                "Test Video",
                "TestUser",
                meta
        );

        User user = new User();
        user.setUsername("TestUser");

        Tag tag1 = new Tag();
        tag1.setName("tag1");
        Tag tag2 = new Tag();
        tag2.setName("tag2");
        Tag tag3 = new Tag();
        tag3.setName("tag3");
        Tag tag4 = new Tag();
        tag4.setName("tag4");

        when(videoRepository.existsById(1L)).thenReturn(false);
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(tagRepository.findByName("tag1")).thenReturn(Optional.of(tag1));
        when(tagRepository.findByName("tag2")).thenReturn(Optional.of(tag2));
        when(tagRepository.findByName("tag3")).thenReturn(Optional.of(tag3));
        when(tagRepository.findByName("tag4")).thenReturn(Optional.of(tag4));

        // When
        videoImportService.importVideo(dto, "test.json");

        // Then
        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository).save(videoCaptor.capture());

        Video savedVideo = videoCaptor.getValue();
        assertEquals(4, savedVideo.getTags().size());
        assertTrue(savedVideo.getTags().contains(tag1));
        assertTrue(savedVideo.getTags().contains(tag2));
        assertTrue(savedVideo.getTags().contains(tag3));
        assertTrue(savedVideo.getTags().contains(tag4));
    }
}

