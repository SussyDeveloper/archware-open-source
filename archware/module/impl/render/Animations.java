package archware.module.impl.render;

import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;

public class Animations extends Module {

    static String[] animations = {"Exhi","Exhibobo","Swong", "Swang", "Swank","Push", "Slide", "1.7", "1.8"};
    public static ModeSetting mode = new ModeSetting("Mode", "Exhibobo", animations);
    static NumberSetting slowDown = new NumberSetting("Swing Duration", 1.0, 0.1, 3.0, 0.1, true);
    static NumberSetting xPos = new NumberSetting("X", 0.0, -1, 1, 0.05, true);
    static NumberSetting yPos = new NumberSetting("Y", 0.0, -1, 1, 0.05, true);
    static NumberSetting zPos = new NumberSetting("z", 0.0, -1, 1, 0.05, true);

    public Animations() {
        super("Animations", "", 0, Category.RENDER);
        addSettings(mode, slowDown);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        toggle();
        NotificationManager.queue("Animations", "You cannot enable it, because the animations module is currently enabled", NotificationType.WARNING, 3000);
    }

    public static NumberSetting getSlowDown() {
        return slowDown;
    }

}
