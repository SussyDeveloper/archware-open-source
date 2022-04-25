package archware.module.impl.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.client.C03PacketPlayer;
import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.utils.packet.PacketUtils;

public class NoFall extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Packet", "Edit");

    public NoFall() {
        super("NoFall", "", 0, Category.MOVEMENT);
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventUpdate){
            setDisplayName("NoFall " + ChatFormatting.GRAY + mode.getSelected());
        }

        if(event instanceof EventMotion){
            if (event.isPre()) {
                if (mc.thePlayer.fallDistance > 3.0) {
                    switch (mode.getSelected()) {
                        case "Vanilla":
                            ((EventMotion) event).setOnGround(true);
                            break;
                        case "Packet":
                            PacketUtils.sendPacket(new C03PacketPlayer(true));
                            break;
                    }
                    mc.thePlayer.fallDistance = 0;
                }
            }
        }
    }
}
