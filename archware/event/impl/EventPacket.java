package archware.event.impl;

import net.minecraft.network.Packet;
import archware.event.Event;
import archware.event.EventDirection;

public class EventPacket extends Event {
    public Packet packet;

    public EventPacket(Packet packet, EventDirection direction) {
        this.packet = packet;
        this.direction = direction;
    }


    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
