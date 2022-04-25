package archware.module.impl.other;

import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;

public class AntiAim extends Module {

    float pitch, yaw;
    ModeSetting mode = new ModeSetting("Mode", "Gay", "Gay","Unhittable");

    public AntiAim() {
        super("AntiAim", "SUS AMOGUS L:OLOLOOLO BARTIX SKOT CC FATHERLESS!@!!!!!!!!!!!!", 0, Category.OTHER);
        addSettings(mode);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventMotion){
            EventMotion e = (EventMotion) event;
            if(event.isPre()){

                if(mode.is("Unhittable")){
                    pitch = (float) (Math.random() * 180 - 90);
                    yaw = (float) Math.random() * 360;
                }

                e.setYaw(yaw);
                e.setPitch(pitch);
                mc.thePlayer.renderYawOffset = yaw;
                mc.thePlayer.rotationYawHead = yaw;
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
    }
}
