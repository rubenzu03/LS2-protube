package ws.schild.jave;

import java.io.File;
import ws.schild.jave.info.MultimediaInfo;

// Minimal test stub to replace the real JAVE MultimediaObject during tests
public class MultimediaObject {
    private final File file;

    public MultimediaObject(File file) {
        this.file = file;
    }

    public MultimediaInfo getInfo() {
        return new MultimediaInfo();
    }
}

