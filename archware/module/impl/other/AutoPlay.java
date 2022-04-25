package archware.module.impl.other;

import archware.event.Event;
import archware.event.impl.EventPacket;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.NumberSetting;
import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;
import archware.utils.timers.Stopwatch;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class AutoPlay extends Module {

    NumberSetting playdelay = new NumberSetting("AutoPlay Delay", 5000, 1000, 5000, 1000, true);
    Stopwatch delay = new Stopwatch();

    public AutoPlay() {
        super("AutoPlay", "", 0, Category.OTHER);
        addSettings(playdelay);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventPacket){
            EventPacket e = (EventPacket) event;
            if (event.isIncoming()){
                if(e.getPacket() instanceof S02PacketChat){
                    String message = ((S02PacketChat) e.getPacket()).getChatComponent().getUnformattedText();
                    if(message.contains("Deseja jogar")){
                        NotificationManager.queue("Auto Play", "Playing again in " + (int) playdelay.getValue() + "ms", NotificationType.INFO, (int) playdelay.getValue());
                        if(delay.elapsed((long) playdelay.getValue(), true)){
                            mc.thePlayer.sendChatMessage("/play swsolo");
                        }
                    }

                    if(message.contains("sua foi banido")){
                        NotificationManager.queue("Auto Lobby", "Someone has been banned in your game.", NotificationType.INFO, 5000);
                        mc.thePlayer.sendChatMessage("/lobby");
                    }

//                    if(message.contains(mc.thePlayer.getName())){
//                        NotificationManager.queue("Lol", "lol", NotificationType.WARNING, 3000);
//                    }
                }
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
