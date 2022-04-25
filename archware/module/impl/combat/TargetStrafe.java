package archware.module.impl.combat;

import archware.event.Event;
import archware.event.impl.Event3D;
import archware.event.impl.EventMotion;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.Wrapper;
import archware.utils.render.RenderUtils;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TargetStrafe extends Module {

    static BoolSetting flightProperty = new BoolSetting("Flight", false);
    static BoolSetting speedProperty = new BoolSetting("Speed", true);
    BoolSetting adaptiveProperty = new BoolSetting("Adaptive", true);
    BoolSetting behind = new BoolSetting("Behind", false);
    BoolSetting keepRangeValue = new BoolSetting("Keep Range", false);
    NumberSetting radiusProperty = new NumberSetting("Radius", 1.0D, 0.1D, 4.0D, 0.1D, false);
    NumberSetting shapeValue = new NumberSetting("Shape", 12, 0, 30, 1, true);
    NumberSetting red = new NumberSetting("Red", 71, 0, 255, 1, true);
    NumberSetting green = new NumberSetting("Green", 255, 0, 255, 1, true);
    NumberSetting blue = new NumberSetting("Blue", 0, 0, 255, 1, true);
    NumberSetting pointsp = new NumberSetting("Points", 12, 4, 90, 1, true);
    NumberSetting lineWidth = new NumberSetting("Line Width", 3, 0.5, 10, 0.5, false);
    private final List<Vector3f> points = new ArrayList<>();
    int color;
    static TargetStrafe instance = new TargetStrafe();
    public byte direction;

    public TargetStrafe() {
        super("TargetStrafe", "", 0, Category.COMBAT);
        addSettings(radiusProperty, pointsp, lineWidth, red, green, blue, speedProperty, flightProperty);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventMotion){
            if (event.isPre()) {
                if (Wrapper.getPlayer().isCollidedHorizontally) {
                    direction = (byte) -direction;
                    return;
                }

                if (Wrapper.getGameSettings().keyBindLeft.isKeyDown()) {
                    direction = 1;
                    return;
                }

                if (Wrapper.getGameSettings().keyBindRight.isKeyDown())
                    direction = -1;
            }
        }

        if(event instanceof Event3D){
            Event3D e = (Event3D) event;
            KillAura killAura = new KillAura();
            if (killAura.getTarget() != null) {
                RenderUtils.drawLinesAroundPlayer(killAura.getTarget(),
                        radiusProperty.getValue(),
                        e.getPartialTicks(),
                        (int) pointsp.getValue(),
                        (float) lineWidth.getValue(),
                        new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue()).getRGB());
            }
        }
    }


    public NumberSetting getRadiusProperty() {
        return radiusProperty;
    }

    public static BoolSetting getFlightProperty() {
        return flightProperty;
    }

    public static BoolSetting getSpeedProperty() {
        return speedProperty;
    }
}
