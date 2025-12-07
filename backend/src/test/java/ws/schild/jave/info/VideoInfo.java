package ws.schild.jave.info;

// Minimal test stub for VideoInfo
public class VideoInfo {
    private VideoSize size;

    public VideoInfo() {
        this.size = new VideoSize(1920, 1080);
    }

    public VideoSize getSize() {
        return size;
    }

    public void setSize(VideoSize size) {
        this.size = size;
    }
}
