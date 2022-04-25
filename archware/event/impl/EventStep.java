package archware.event.impl;

import archware.event.Event;

public class EventStep extends Event {

    private float stepHeight;
    private double heightStepped;

    public EventStep(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public double getHeightStepped() {
        return heightStepped;
    }

    public void setHeightStepped(double heightStepped) {
        this.heightStepped = heightStepped;
    }

}
