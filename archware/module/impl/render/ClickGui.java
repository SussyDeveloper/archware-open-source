package archware.module.impl.render;

import org.lwjgl.input.Keyboard;
import archware.Client;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.NumberSetting;

public class ClickGui extends Module {

    public static BoolSetting cursor = new BoolSetting("Cursor", false);
    public static NumberSetting red = new NumberSetting("Red", 255, 0, 255, 1, true);
    public static NumberSetting green = new NumberSetting("Green", 0, 0, 255, 1, true);
    public static NumberSetting blue = new NumberSetting("Blue", 0, 0, 255, 1, true);
    public static BoolSetting background = new BoolSetting("Background", false);

    public ClickGui() {
        super("ClickGUI", "gfgdf", Keyboard.KEY_RSHIFT, Category.RENDER);
        addSettings(cursor, background, red, green, blue);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Client.clickGUI);
        toggle();
    }
}
