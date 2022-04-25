package archware.module.impl.player;

import archware.Client;
import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.movement.MoveUtils;
import archware.utils.player.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.util.Vec3;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AntiFall extends Module {

    //TODO: Fix AntiFall (AntiVoid)

    ModeSetting mode = new ModeSetting("Mode", "Position", "Position", "GroundSpoof", "Verus");
    NumberSetting distance = new NumberSetting("Distance", 3, 1F, 10F, 0.1F, false);

    private boolean saved;
    private boolean aBoolean, aBoolean2;
    private Vec3 lastGround = new Vec3(0, 0, 0);

    private static final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private Vec3 lastServerPosition = null;


    public AntiFall() {
        super("AntiFall", "", 0, Category.PLAYER);
        addSettings(mode, distance);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        saved = false;
    }

    @Override
    public void onEvent(Event e) {
        super.onEvent(e);
        if (e instanceof EventMotion) {
            EventMotion event = (EventMotion) e;
            if (e.isPre()) {
                if (mc.thePlayer.onGround)
                    lastGround = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

                final boolean isBlockUnder = PlayerUtil.isBlockUnder();
                if (!isBlockUnder && mc.thePlayer.fallDistance > distance.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.thePlayer.capabilities.isFlying && !Client.moduleManager.getModuleByName("Flight").isEnabled()) {
                    if (shouldLagback()) {
                        switch (mode.getSelected()) {
                            case "Position": {
                                event.setY(event.getY() + mc.thePlayer.fallDistance);
                                break;
                            }
                        }
                    }
                }

                if (!saved) {
                    saved = true;
                }
            } else
                saved = false;
        }
    }


    public boolean shouldLagback() {
        return mc.thePlayer.fallDistance > distance.getValue() && MoveUtils.isOverVoid();
    }
}
