package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideosControllerTest {

    @InjectMocks
    VideoController videosController;

    @Mock
    VideoService videoService;

    @Test
    void getVideos() {
        VideoDTO v1 = new VideoDTO(1L, "video 1", 0f, 0f, 0f, null, null, null, null, null);
        VideoDTO v2 = new VideoDTO(2L, "video 2", 0f, 0f, 0f, null, null, null, null, null);
        when(videoService.getVideos()).thenReturn(List.of(v1, v2));

        List<VideoDTO> body = videosController.getVideos().getBody();
        assert body != null;
        assertEquals(List.of("video 1", "video 2"), body.stream().map(VideoDTO::title).toList());
    }
}