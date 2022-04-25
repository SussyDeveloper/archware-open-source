package archware.module.impl.other;

import archware.Client;
import archware.event.Event;
import archware.event.impl.Event2D;
import archware.event.impl.EventPacket;
import archware.module.Category;
import archware.module.Module;
import archware.utils.render.RenderUtils;
import archware.utils.timers.Stopwatch;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class LagDetector extends Module {

    Stopwatch timer = new Stopwatch();
    static boolean cannotfly;

    public LagDetector() {
        super("LagDetector", "", 0, Category.OTHER);
        setHidden(true);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if (event instanceof Event2D) {
            boolean scaffold = Client.moduleManager.getModuleByName("Scaffold").isEnabled();
            ScaledResolution sr = new ScaledResolution(mc);
            if (this.timer.elapsed(1000L)) {
                if (this.timer.elapsed(150L)) {
                    cannotfly = true;
                    RenderUtils.drawImage(new ResourceLocation("archware/textures/lag2.png"), sr.getScaledWidth() / 2 - 20, sr
                            .getScaledHeight() / 2 - (scaffold ? 85 : 65), 40, 40);
                } else {
                    RenderUtils.drawImage(new ResourceLocation("archware/textures/lag.png"), sr.getScaledWidth() / 2 - 20, sr
                            .getScaledHeight() / 2 - (scaffold ? 85 : 65), 40, 40);
                }
                RenderUtils.drawOutlinedString("Detected", sr.getScaledWidth() / 2.0F - mc.fontRendererObj.getStringWidth("Detected") / 2.0F - 3.0F, sr
                        .getScaledHeight() / 2.0F - (scaffold ? 40 : 20), (new Color(255, 127, 0)).getRGB(), (new Color(0, 0, 0)).getRGB());
            }
        }

        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (event.isIncoming()) {
                Packet<?> receive = e.getPacket();
                if (!(receive instanceof net.minecraft.network.play.server.S02PacketChat))
                    this.timer.reset();
                cannotfly = false;
            }
        }
    }

    public static boolean isCannotfly() {
        return cannotfly;
    }
}
