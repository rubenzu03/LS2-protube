package ws.schild.jave.info;

// Minimal test stub for MultimediaInfo
public class MultimediaInfo {
    private VideoInfo video;
    private long duration;

    public MultimediaInfo() {
        this.video = new VideoInfo();
        this.duration = 0;
    }

    public VideoInfo getVideo() {
        return video;
    }

    public void setVideo(VideoInfo video) {
        this.video = video;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
