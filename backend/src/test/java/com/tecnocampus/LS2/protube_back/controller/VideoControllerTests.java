package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
@TestPropertySource(properties = {"pro_tube.store.dir=${java.io.tmpdir}"})
public class VideoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private VideoService videoService;

    @BeforeEach
    void setup() {
        // ensure controller has the temp directory as storeDir (in case property not picked)
        VideoController controller = webApplicationContext.getBean(VideoController.class);
        String tmp = System.getProperty("java.io.tmpdir");
        ReflectionTestUtils.setField(controller, "storeDir", tmp);
    }

    @Test
    void getVideos_returnsList() throws Exception {
        VideoDTO v1 = new VideoDTO(1L, "Title 1", 640f, 480f, 10f, "desc", "file1.mp4", 1L, null, null, null);
        VideoDTO v2 = new VideoDTO(2L, "Title 2", 1280f, 720f, 20f, "desc2", "file2.mp4", 2L, null, null, null);
        given(videoService.getVideos()).willReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Title 1"));
    }

    @Test
    void getVideoById_returnsVideo() throws Exception {
        VideoDTO v = new VideoDTO(5L, "Some title", 320f, 240f, 5f, "d", "some.mp4", 1L, null, null, null);
        when(videoService.getVideoById(5L)).thenReturn(v);

        mockMvc.perform(get("/api/videos/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Some title"));
    }

    @Test
    void createVideo_returnsCreatedWithBody() throws Exception {
        VideoDTO v = new VideoDTO(null, "New", 100f, 100f, 1f, "desc", "new.mp4", null, null, null, null);
        // controller returns the same DTO body, it calls service but responds with the passed dto
        when(videoService.createVideo(any())).thenReturn(new VideoDTO(10L, "New", 100f, 100f, 1f, "desc", "new.mp4", null, null, null, null));

        mockMvc.perform(post("/api/videos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(v)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New"));
    }

    @Test
    void deleteVideo_returnsNoContent() throws Exception {
        VideoDTO v = new VideoDTO(3L, "ToDelete", 1f, 1f, 1f, "d", "d.mp4", null, null, null, null);
        when(videoService.deleteVideo(3L)).thenReturn(v);

        mockMvc.perform(delete("/api/videos/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void streamVideo_returnsNotFound_whenNoFilenameOrMissingFile() throws Exception {
        VideoDTO vNoFile = new VideoDTO(7L, "NoFile", 1f, 1f, 1f, "d", null, null, null, null, null);
        when(videoService.getVideoById(7L)).thenReturn(vNoFile);

        mockMvc.perform(get("/api/videos/stream/7"))
                .andExpect(status().isNotFound());

        VideoDTO vWithFilename = new VideoDTO(8L, "MissingFile", 1f, 1f, 1f, "d", "does-not-exist.mp4", null, null, null, null);
        when(videoService.getVideoById(8L)).thenReturn(vWithFilename);

        mockMvc.perform(get("/api/videos/stream/8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void streamVideo_returnsFile_whenExists() throws Exception {
        // create temp file in the controller's store dir
        String tmp = System.getProperty("java.io.tmpdir");
        File f = File.createTempFile("videotest-", ".mp4", new File(tmp));
        f.deleteOnExit();
        Files.write(f.toPath(), "dummydata".getBytes());

        VideoDTO v = new VideoDTO(9L, "HasFile", 1f, 1f, 1f, "d", f.getName(), null, null, null, null);
        when(videoService.getVideoById(9L)).thenReturn(v);

        mockMvc.perform(get("/api/videos/stream/9"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"" + f.getName() + "\""))
                .andExpect(header().string("Content-Type", "video/mp4"));
    }
}
