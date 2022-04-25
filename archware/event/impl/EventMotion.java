package archware.event.impl;

import archware.event.Event;

public class EventMotion extends Event {

    public boolean onGround;
    public double x, y, z;
    public float yaw, pitch;
    private float prevYaw;
    private float prevPitch;

    public EventMotion(double x, double y, double z, float yaw, float pitch, float prevYaw, float prevPitch,boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.prevPitch = prevPitch;
        this.prevYaw = prevYaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPrevPitch() {
        return this.prevPitch;
    }

    public float getPrevYaw() {
        return this.prevYaw;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}

