package archware.module.impl.combat;

import archware.Client;
import archware.event.impl.EventPacket;
import archware.event.impl.EventUpdate;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import net.minecraft.network.play.server.*;
import archware.event.Event;
import archware.module.Category;
import archware.module.Module;

public class Velocity extends Module {

    NumberSetting horizontal = new NumberSetting("Horizontal", 90, 0, 100, 1, true);
    NumberSetting vertical = new NumberSetting("Vertical", 100, 0, 100, 1,true);
    NumberSetting chance = new NumberSetting("Chance", 70, 0, 100, 1, true);
    ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Custom");

    public Velocity() {
        super("Velocity", "", 0, Category.COMBAT);
        addSettings(horizontal, vertical, chance, mode);
    }
    public void onEvent(Event event) {
        if(event instanceof EventPacket && event.isIncoming()){
            if(mode.is("Cancel")) {
                if (((EventPacket) event).getPacket() instanceof S12PacketEntityVelocity || ((EventPacket) event).getPacket() instanceof S27PacketExplosion) {
                    event.setCancelled(true);
                }
            }
        }
        if(event instanceof EventUpdate){
            if(mode.is("Custom")){
                if(isPlayerInGame() && mc.thePlayer.maxHurtTime > 0 && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime){
                    if(chance.getValue() != 100){
                        double ch = Math.random();
                        if (ch >= chance.getValue() / 100.0D) {
                            return;
                        }
                    }
                    if(horizontal.getValue() != 100){
                        mc.thePlayer.motionX *= horizontal.getValue() / 100.0D;
                        mc.thePlayer.motionZ *= horizontal.getValue() / 100.0D;
                    }

                    if (vertical.getValue() != 100.0D) {
                        mc.thePlayer.motionY *= vertical.getValue() / 100.0D;
                    }
                }
            }
        }
    }

    public boolean isPlayerInGame() {
        return mc.thePlayer != null && mc.theWorld != null;
    }
}
