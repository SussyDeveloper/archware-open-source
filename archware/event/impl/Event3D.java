package archware.event.impl;

import archware.event.Event;

public class Event3D extends Event {

    private float partialTicks;

    public Event3D(final float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
