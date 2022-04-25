package archware.event.impl;

import archware.event.Event;

public class EventTick extends Event {

    private final int ticks;

    public EventTick(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
