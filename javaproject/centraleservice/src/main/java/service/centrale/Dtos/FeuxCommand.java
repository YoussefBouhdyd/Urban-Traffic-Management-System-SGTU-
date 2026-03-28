package service.centrale.Dtos;

public class FeuxCommand {
    private int duration;
    private boolean green;

    public FeuxCommand() {
    }

    public FeuxCommand(int duration, boolean green) {
        this.duration = duration;
        this.green = green;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isGreen() {
        return green;
    }

    public void setGreen(boolean green) {
        this.green = green;
    }
}
