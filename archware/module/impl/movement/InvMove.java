package archware.module.impl.movement;

import archware.event.Event;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InvMove extends Module {

    private final KeyBinding[] moveKeys = { mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint };

    public InvMove() {
        super("InvMove", "", 0, Category.MOVEMENT);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if (event instanceof EventUpdate) {
            if (!(mc.currentScreen instanceof GuiChat)) {
                for (KeyBinding moveKey : moveKeys) {
                    moveKey.pressed = Keyboard.isKeyDown(moveKey.getKeyCode());
                }
            }
        }
    }
}
