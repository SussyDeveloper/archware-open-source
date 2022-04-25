package archware.event.impl;

import net.minecraft.client.gui.ScaledResolution;
import archware.event.Event;

public class Event2D extends Event {

    public ScaledResolution sr;

    public Event2D(ScaledResolution sr) {
        this.sr = sr;
    }
}
