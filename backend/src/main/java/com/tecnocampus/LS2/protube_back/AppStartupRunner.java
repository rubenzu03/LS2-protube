package com.tecnocampus.LS2.protube_back;

import com.tecnocampus.LS2.protube_back.services.VideoImportService;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AppStartupRunner.class);

    @Autowired
    VideoService videoService;

    @Autowired
    VideoImportService videoImportService;

    // Inject properties directly using @Value annotations
    @Value("${pro_tube.store.dir}")
    private String storeDirectory;

    @Value("${pro_tube.load_initial_data}")
    private Boolean loadInitialData;

    @Value("${pro_tube.video.max_duration}")
    private Integer maxDuration;

    @Value("${pro_tube.video.default_quality}")
    private String defaultQuality;

    private Path getRootPath() {
        return Paths.get(storeDirectory);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Starting ProTube application...");
        LOG.info("Store directory: {}", getRootPath());
        LOG.info("Load initial data: {}", loadInitialData);
        LOG.info("Max video duration: {} seconds", maxDuration);
        LOG.info("Default video quality: {}", defaultQuality);

        // Should your backend perform any task during the bootstrap, do it here
        if (loadInitialData) {
            LOG.info("Loading initial data from: {}", getRootPath());
            // Add your initial data loading logic here
            // You can use videoService here to load videos from the store directory
            videoImportService.importVideosFromJsonFiles(getRootPath());
        }
    }
}
