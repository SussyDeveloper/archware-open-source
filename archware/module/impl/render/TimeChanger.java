package archware.module.impl.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import archware.event.Event;
import archware.event.impl.Event3D;
import archware.event.impl.EventPacket;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.NumberSetting;

public class TimeChanger extends Module {

    NumberSetting time = new NumberSetting("Time", 18000, 0, 24000, 500, true);

    public TimeChanger() {
        super("TimerChanger", "", 0, Category.RENDER);
        addSettings(time);
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
        if(event instanceof Event3D){
            mc.theWorld.setWorldTime((long) time.getValue());
        }
        if(event instanceof EventPacket && event.isIncoming()){
            if(((EventPacket) event).getPacket() instanceof S03PacketTimeUpdate){
                event.setCancelled(true);
            }
        }
    }
}
