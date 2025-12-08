package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class VideoControllerUploadTest {

    private MockMvc mockMvc;

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoController videoController;

    @BeforeEach
    void setUp() throws Exception {
        // Set the storeDir private field via reflection so controller uses a test directory
        Field storeDirField = VideoController.class.getDeclaredField("storeDir");
        storeDirField.setAccessible(true);
        storeDirField.set(videoController, "target/test-store");

        this.mockMvc = MockMvcBuilders.standaloneSetup(videoController).build();
    }

    @Test
    void uploadVideo_happyPath_returnsCreatedAndCallsService() throws Exception {
        byte[] content = "dummy video content".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", content);

        VideoDTO returned = new VideoDTO(1L, "test", 640f, 480f, 1f, null, "stored.mp4", null, null, null, null);

        when(videoService.createVideo(ArgumentMatchers.any(VideoDTO.class))).thenReturn(returned);

        mockMvc.perform(multipart("/api/videos/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("test"));

        verify(videoService, times(1)).createVideo(ArgumentMatchers.any(VideoDTO.class));
    }

    @Test
    void uploadVideo_missingFile_returnsBadRequest() throws Exception {
        mockMvc.perform(multipart("/api/videos/upload"))
                .andExpect(status().isBadRequest());

        verify(videoService, times(0)).createVideo(any());
    }

    @Test
    void uploadVideo_serviceThrows_returnsInternalServerError() throws Exception {
        byte[] content = "bad content".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", content);

        when(videoService.createVideo(ArgumentMatchers.any(VideoDTO.class))).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(multipart("/api/videos/upload").file(file))
                .andExpect(status().isInternalServerError());

        verify(videoService, times(1)).createVideo(any(VideoDTO.class));
    }
}
