package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    @Test
    void shouldReturnVideoTitles() {
        Video v1 = new Video(); v1.setId(1L); v1.setTitle("video1");
        Video v2 = new Video(); v2.setId(2L); v2.setTitle("video2");
        when(videoRepository.findAll()).thenReturn(List.of(v1, v2));

        List<VideoDTO> result = videoService.getVideos();
        assertEquals(List.of("video1","video2"), result.stream().map(VideoDTO::title).toList());
    }
}