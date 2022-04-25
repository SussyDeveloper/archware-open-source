package archware.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import archware.event.Event;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;

public class Step extends Module {

    ModeSetting mode = new ModeSetting("Mode", "NCP", "NCP");
    public static boolean cancelStep;
    private final double[] offsets = {0.42f, 0.7532f};
    private final float timerWhenStepping = 1.0f / (offsets.length + 1);

    public Step() {
        super("Step", "", 0, Category.MOVEMENT);
        addSettings(mode);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventUpdate){
            if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) {
                mc.thePlayer.stepHeight = 2.0f;
            }
            else {
                mc.thePlayer.stepHeight = 0.5f;
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
        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.stepHeight = 0.5f;
    }
}
