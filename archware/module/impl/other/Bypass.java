package archware.module.impl.other;

import archware.event.Event;
import archware.event.impl.EventPacket;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.ui.notification.Notification;
import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;
import archware.utils.packet.PacketUtils;
import archware.utils.timers.Stopwatch;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S40PacketDisconnect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Bypass extends Module {

    static ModeSetting mode = new ModeSetting("Mode", "MushMC", "MushMC", "PingSpoof");
    public static ArrayList<Packet> packets;
    private LinkedList<Packet> packetQueue;
    private List<Packet> packetList;
    ArrayList<Packet> transactions;
    int currentTransaction;
    Stopwatch timer = new Stopwatch();

    public Bypass() {
        super("Bypass", "", 0, Category.OTHER);
        setHidden(true);
        addSettings(mode);
        this.packetQueue = new LinkedList<>();
        this.packetList = new ArrayList<>();
        this.transactions = new ArrayList<>();
        packets = new ArrayList<>();
        this.currentTransaction = 0;
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.isIncoming()) {
                if (e.getPacket() instanceof S02PacketChat) {
                    String message = ((S02PacketChat) e.getPacket()).getChatComponent().getUnformattedText();
                    if (mode.is("MushMC")) {
                        if (message.equals("Cabines abertas! VAI!")) {
                            timer.reset();
                            NotificationManager.queue("SkyWars Warning", "Don't Fly until hide this notification!", NotificationType.WARNING, 5000);
//                            if(timer.elapsed(5000)){
//                                NotificationManager.queue("5 seconds has passed!", "You can now use Flight!", NotificationType.SUCCESS, 2000);
//                            }
                        }
                    }
                }
            }

            if(e.isOutgoing()){
                final Packet<?> p = e.getPacket();
                switch (mode.getSelected()){
                    case "MushMC":{
                        if(e.getPacket() instanceof C03PacketPlayer){
                            final C03PacketPlayer c03 = e.getPacket();
                            c03.setY(c03.getY() + 0.015625);
                        }

//                        if (p instanceof C0BPacketEntityAction) {
//                            final C0BPacketEntityAction c0B = (C0BPacketEntityAction) p;
//
//                            if (c0B.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
//                                if (EntityPlayerSP.isServerSprintState()) {
//                                    PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
//                                    EntityPlayerSP.setServerSprintState(false);
//                                }
//                                event.setCancelled(true);
//                            }
//
//                            if (c0B.getAction().equals(C0BPacketEntityAction.Action.STOP_SPRINTING)) {
//                                event.setCancelled(true);
//                            }
//                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.packetQueue.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.currentTransaction = 0;
        this.transactions.clear();
        this.packetQueue.clear();
        this.packetList.clear();
        packets.clear();
    }

    public static ModeSetting getMode() {
        return mode;
    }
}
