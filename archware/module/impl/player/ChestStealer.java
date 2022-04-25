package archware.module.impl.player;

import archware.module.settings.impl.BoolSetting;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import org.lwjgl.input.Keyboard;
import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.event.impl.EventPacket;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;
import archware.utils.InventoryUtils;
import archware.utils.Wrapper;
import archware.utils.timers.Stopwatch;

public class ChestStealer extends Module {

    static ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Render");
    static NumberSetting clickDelay = new NumberSetting("Click Delay", 150, 10, 500, 10, true);
    static NumberSetting closeDelay = new NumberSetting("Close Delay", 150, 10, 500, 10, true);
    BoolSetting byTitle = new BoolSetting("Title Check", true);
    BoolSetting notification = new BoolSetting("Notification", false);
    Stopwatch timer = new Stopwatch();

    public ChestStealer() {
        super("ChestStealer", "", Keyboard.KEY_X, Category.PLAYER);
        addSettings(mode, clickDelay, closeDelay, byTitle);
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
        if(event instanceof EventPacket){
            if(event.isIncoming()){
                if(((EventPacket) event).getPacket() instanceof S2DPacketOpenWindow){
                    timer.reset();
                }
            }
        }

        if(event instanceof EventMotion){
            if(event.isPre()){
                if (Wrapper.getCurrentScreen() instanceof GuiChest) {
                    GuiChest chest = (GuiChest) Wrapper.getCurrentScreen();
                    if (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Chest") && byTitle.isEnable() || !chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Chest") && !byTitle.isEnable()) {
                        if(mode.is("Render")){
                            mc.mouseHelper.grabMouseCursor();
                            mc.inGameHasFocus = true;
                        }
                        if (isInventoryFull() || isChestEmpty(chest)) {
                            if (timer.elapsed((long) closeDelay.getValue())){
                                Wrapper.getPlayer().closeScreen();
                                if(notification.isEnable())
                                    NotificationManager.queue("Close", "Closed Chest", NotificationType.INFO, 1000);
                            }
                            return;
                        }

                        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                            if (timer.elapsed((long) clickDelay.getValue())) {
                                if (InventoryUtils.isValid(chest.getLowerChestInventory().getStackInSlot(i))) {
                                    InventoryUtils.windowClick(
                                            chest.inventorySlots.windowId, i, 0, InventoryUtils.ClickType.SHIFT_CLICK);
                                    timer.reset();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isChestEmpty(GuiChest chest) {
        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
            if (InventoryUtils.isValid(chest.getLowerChestInventory().getStackInSlot(i)))
                return false;
        }

        return true;
    }

    private boolean isInventoryFull() {
        for (int i = 9; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                return false;
        }
        return true;
    }
}
