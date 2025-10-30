package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.*;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.domain.Video;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VideoServiceTest {

    @Test
    void shouldGoToFolderVideos() {
        VideoRepository repo = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

        Video v1 = new Video();
        v1.setId(1L);
        v1.setTitle("video1");

        Video v2 = new Video();
        v2.setId(2L);
        v2.setTitle("video2");

        List<Video> videos = List.of(v1, v2);
        Mockito.when(repo.findAll()).thenReturn(videos);

        VideoService videoService = new VideoService(repo, userRepository, categoryRepository, tagRepository, commentRepository);

        assertEquals(List.of("video1", "video2"),
                videoService.getVideos().stream().map(VideoDTO::title).toList());
    }
    @Test
    void getVideos_emptyList_returnsEmpty() {
        VideoRepository repo = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

        Mockito.when(repo.findAll()).thenReturn(List.of());

        VideoService service = new VideoService(repo, userRepository, categoryRepository, tagRepository, commentRepository);

        assertTrue(service.getVideos().isEmpty());
    }

    @Test
    void getVideoById_notFound_returnsNull() {
        VideoRepository repo = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

        Mockito.when(repo.findById(1L)).thenReturn(Optional.empty());

        VideoService service = new VideoService(repo, userRepository, categoryRepository, tagRepository, commentRepository);

        assertNull(service.getVideoById(1L));
    }

    @Test
    void createVideo_savesAndReturnsDTO() {
        VideoRepository repo = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

        VideoDTO dto = new VideoDTO(null, "t", 0f, 0f, 0f, "d", "f", null, null, null, null);
        Video saved = new Video(dto);
        saved.setId(5L);

        Mockito.when(repo.save(ArgumentMatchers.any(Video.class))).thenReturn(saved);

        VideoService service = new VideoService(repo, userRepository, categoryRepository, tagRepository, commentRepository);

        VideoDTO result = service.createVideo(dto);
        assertNotNull(result);
        assertEquals(5L, result.id());
        assertEquals("t", result.title());
    }

    @Test
    void updateVideo_missingAssociations_stillSavesAndReturnsDTO() {
        VideoRepository repo = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);

        Video existing = new Video();
        existing.setId(10L);
        existing.setTitle("old");

        Mockito.when(repo.findById(10L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        Mockito.when(tagRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        Mockito.when(commentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        Mockito.when(repo.save(ArgumentMatchers.any(Video.class))).thenAnswer(i -> i.getArgument(0));

        VideoDTO dto = new VideoDTO(10L, "new", 0f, 0f, 0f, "desc", "file", null, null, null, null);

        VideoService service = new VideoService(repo, userRepository, categoryRepository, tagRepository, commentRepository);

        VideoDTO result = service.updateVideo(10L, dto);
        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("new", result.title());
    }
}
