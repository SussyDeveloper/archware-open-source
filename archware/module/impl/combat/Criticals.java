package archware.module.impl.combat;

import archware.Client;
import archware.event.Event;
import archware.event.impl.EventAttack;
import archware.event.impl.EventMotion;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.packet.PacketUtils;
import archware.utils.timers.Stopwatch;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.Arrays;

public class Criticals extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet");
    NumberSetting delay = new NumberSetting("Delay", 500, 0, 1000, 25, true);
    Stopwatch timer = new Stopwatch();
    private boolean attacked;

    public Criticals() {
        super("Criticals", "", 0, Category.COMBAT);
        addSettings(delay);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventUpdate){
            setDisplayName("Criticals " + ChatFormatting.GRAY + mode.getSelected());
        }

        if(event instanceof EventMotion){
            if((timer.elapsed((long) delay.getValue())) && !Client.moduleManager.getModuleByName("Speed").isEnabled() && KillAura.target != null && Client.moduleManager.getModuleByName("KillAura").isEnabled()) {
                if ("Packet".equals(mode.getSelected())) {
                    if (mc.thePlayer.onGround) {
                        final double[] values = {0.0625, 0.001 - (Math.random() / 10000)}; // CARPET VALUE
                        for (final double d : values) {
                            PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
//                            Client.message("Criticals " + d);
                        }
                    }
                }
                timer.reset();
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        timer.reset();
    }
}
