package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.domain.Video;

class VideoServiceTest {

    @Test
    void shouldGoToFolderVideos() {
        VideoRepository repo = Mockito.mock(VideoRepository.class);

        Video v1 = new Video();
        v1.setId(1L);
        v1.setTitle("video1");

        Video v2 = new Video();
        v2.setId(2L);
        v2.setTitle("video2");

        List<Video> videos = List.of(v1, v2);
        Mockito.when(repo.findAll()).thenReturn(videos);

        VideoService videoService = new VideoService(repo);

        assertEquals(List.of("video1", "video2"),
                videoService.getVideos().stream().map(Video::getTitle).toList());
    }

        List<VideoDTO> result = videoService.getVideos();
        assertEquals(List.of("video1","video2"), result.stream().map(VideoDTO::title).toList());
    }
}