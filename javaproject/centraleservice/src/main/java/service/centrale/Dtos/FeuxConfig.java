package service.centrale.Dtos;

public class FeuxConfig {
    private int duration;
    private boolean segmentGreen;

    public FeuxConfig() {
    }

    public FeuxConfig(int duration, boolean segmentGreen) {
        this.duration = duration;
        this.segmentGreen = segmentGreen;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isSegmentGreen() {
        return segmentGreen;
    }

    public void setSegmentGreen(boolean segmentGreen) {
        this.segmentGreen = segmentGreen;
    }
}
