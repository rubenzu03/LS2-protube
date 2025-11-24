package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.security.JwtAuthenticationFilter;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = false)
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

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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

    @Test
    void getThumbnail_returnsNotFound_whenNoFilenameOrMissingFile() throws Exception {
        VideoDTO vNoFile = new VideoDTO(7L, "NoFile", 1f, 1f, 1f, "d", null, null, null, null, null);
        when(videoService.getVideoById(7L)).thenReturn(vNoFile);

        mockMvc.perform(get("/api/videos/thumbnail/7"))
                .andExpect(status().isNotFound());

        VideoDTO vWithFilename = new VideoDTO(8L, "MissingFile", 1f, 1f, 1f, "d", "does-not-exist.mp4", null, null, null, null);
        when(videoService.getVideoById(8L)).thenReturn(vWithFilename);

        mockMvc.perform(get("/api/videos/thumbnail/8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getThumbnail_returnsFile_whenExists_withDifferentExtensions() throws Exception {
        String tmp = System.getProperty("java.io.tmpdir");

        File fWebp = File.createTempFile("thumbtest-webp-", ".webp", new File(tmp));
        File fJpg = File.createTempFile("thumbtest-jpg-", ".jpg", new File(tmp));
        File fPng = File.createTempFile("thumbtest-png-", ".png", new File(tmp));
        fWebp.deleteOnExit(); fJpg.deleteOnExit(); fPng.deleteOnExit();

        // create files named base of video filenames
        String base1 = "vidwebp" + System.nanoTime();
        File targetWebp = new File(tmp, base1 + ".webp");
        Files.write(targetWebp.toPath(), "d".getBytes());
        targetWebp.deleteOnExit();

        String base2 = "vidjpg" + System.nanoTime();
        File targetJpg = new File(tmp, base2 + ".jpg");
        Files.write(targetJpg.toPath(), "d".getBytes());
        targetJpg.deleteOnExit();

        String base3 = "vidpng" + System.nanoTime();
        File targetPng = new File(tmp, base3 + ".png");
        Files.write(targetPng.toPath(), "d".getBytes());
        targetPng.deleteOnExit();

        VideoDTO v1 = new VideoDTO(21L, "t1", 1f,1f,1f, "d", base1 + ".mp4", null, null, null, null);
        VideoDTO v2 = new VideoDTO(22L, "t2", 1f,1f,1f, "d", base2 + ".mp4", null, null, null, null);
        VideoDTO v3 = new VideoDTO(23L, "t3", 1f,1f,1f, "d", base3 + ".mp4", null, null, null, null);

        when(videoService.getVideoById(21L)).thenReturn(v1);
        when(videoService.getVideoById(22L)).thenReturn(v2);
        when(videoService.getVideoById(23L)).thenReturn(v3);

        // webp
        mockMvc.perform(get("/api/videos/thumbnail/21"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".webp")))
                .andExpect(header().string("Content-Type", "image/webp"));

        // jpg
        mockMvc.perform(get("/api/videos/thumbnail/22"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".jpg")))
                .andExpect(header().string("Content-Type", "image/jpeg"));

        // png
        mockMvc.perform(get("/api/videos/thumbnail/23"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString(".png")))
                .andExpect(header().string("Content-Type", "image/png"));
    }

    @Test
    void listThumbnails_returnsOnlyExistingThumbnails() throws Exception {
        String tmp = System.getProperty("java.io.tmpdir");

        String baseA = "listthumbA" + System.nanoTime();
        File tA = new File(tmp, baseA + ".jpg");
        Files.write(tA.toPath(), "d".getBytes());
        tA.deleteOnExit();

        String baseB = "listthumbB" + System.nanoTime();
        // don't create file for B -> should be skipped

        VideoDTO a = new VideoDTO(31L, "a", 1f,1f,1f,"d", baseA + ".mp4", null, null, null, null);
        VideoDTO b = new VideoDTO(32L, "b", 1f,1f,1f,"d", baseB + ".mp4", null, null, null, null);
        VideoDTO c = new VideoDTO(33L, "c", 1f,1f,1f,"d", null, null, null, null, null);

        when(videoService.getVideos()).thenReturn(List.of(a, b, c));

        mockMvc.perform(get("/api/videos/thumbnails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(31));
    }
}
