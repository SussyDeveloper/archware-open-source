package archware.module.impl.render;

import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;

import static org.lwjgl.opengl.GL11.*;

public class Chams extends Module {

    static ModeSetting color = new ModeSetting("Color", "Custom", "Custom", "HUD", "Fade");
    static NumberSetting red = new NumberSetting("Red", 255, 0, 255, 1, true);
    static NumberSetting green = new NumberSetting("Green", 255, 0, 255, 1, true);
    static NumberSetting blue = new NumberSetting("Blue", 255, 0, 255, 1, true);

    public Chams() {
        super("Chams", "", 0, Category.RENDER);
        addSettings(color, red, green, blue);
    }

//    public static void preRender(Chams instance, Entity entity) {
//        glDisable(GL_TEXTURE_2D);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_BLEND);
//        glDisable(GL_LIGHTING);
//        glEnable(GL_POLYGON_OFFSET_FILL);
//        glPolygonOffset(0.0F, -1000000.0F);
//        OpenGlHelper.setLightmapTextureCoords(1, 240.0F, 240.0F);
//        glDepthMask(false);
//
//        if (instance.rainbow.getValue()) {
//            int rgb = RenderingUtils.getRainbowFromEntity(entity, 6000, false);
//            int r = (rgb >> 16) & 0xFF;
//            int g = (rgb >> 8) & 0xFF;
//            int b = (rgb) & 0xFF;
//            int a = (int) (instance.occludedColor[3] * 255.0F);
//            rgb = ((r & 0xFF) << 16) |
//                    ((g & 0xFF) << 8) |
//                    (b & 0xFF) |
//                    ((a & 0xFF) << 24);
//            RenderingUtils.color(rgb);
//        } else {
//            float[] rgb = instance.occludedColor;
//            glColor4f(rgb[0], rgb[1], rgb[2], rgb[3]);
//        }

    public static ModeSetting getColor() {
        return color;
    }

    public static NumberSetting getRed() {
        return red;
    }

    public static NumberSetting getGreen() {
        return green;
    }

    public static NumberSetting getBlue() {
        return blue;
    }
}
