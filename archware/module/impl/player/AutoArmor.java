package archware.module.impl.player;

import archware.event.Event;
import archware.event.impl.*;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.InventoryUtils;
import archware.utils.timers.Stopwatch;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;


public class AutoArmor extends Module {

    NumberSetting delay = new NumberSetting("Delay", 150, 0, 500, 50, true);
    BoolSetting invOnly = new BoolSetting("Inv Only", false);
    Stopwatch timer = new Stopwatch();

    public AutoArmor() {
        super("AutoArmor", "", 0, Category.PLAYER);
        addSettings(delay, invOnly);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventMotion){
            if(event.isPost())
                return;
            if ((invOnly.isEnable() && !(mc.currentScreen instanceof GuiInventory)) /*|| (onlyWhileNotMoving.isEnabled() && MovementUtils.isMoving())*/) {
                return;
            }
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                timer.reset();
            }
            if (timer.elapsed((long) delay.getValue())) {
                for (int armorSlot = 5; armorSlot < 9; armorSlot++) {
                    if (equipBest(armorSlot)) {
                        timer.reset();
                        break;
                    }
                }
            }
        }
    }

    private boolean equipBest(int armorSlot) {
        int equipSlot = -1, currProt = -1;
        ItemArmor currItem = null;
        ItemStack slotStack = mc.thePlayer.inventoryContainer.getSlot(armorSlot).getStack();
        if (slotStack != null && slotStack.getItem() instanceof ItemArmor) {
            currItem = (ItemArmor) slotStack.getItem();
            currProt = currItem.damageReduceAmount
                    + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, mc.thePlayer.inventoryContainer.getSlot(armorSlot).getStack());
        }
        // find best piece
        for (int i = 9; i < 45; i++) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (is != null && is.getItem() instanceof ItemArmor) {
                int prot = ((ItemArmor) is.getItem()).damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, is);
                if ((currItem == null || currProt < prot) && isValidPiece(armorSlot, (ItemArmor) is.getItem())) {
                    currItem = (ItemArmor) is.getItem();
                    equipSlot = i;
                    currProt = prot;
                }
            }
        }
        // equip best piece (if there is a better one)
        if (equipSlot != -1) {
            if (slotStack != null) {
                InventoryUtils.drop(armorSlot);
            } else {
                InventoryUtils.click(equipSlot, 0, true);
            }
            return true;
        }
        return false;
    }

    private boolean isValidPiece(int armorSlot, ItemArmor item) {
        String unlocalizedName = item.getUnlocalizedName();
        return armorSlot == 5 && unlocalizedName.startsWith("item.helmet")
                || armorSlot == 6 && unlocalizedName.startsWith("item.chestplate")
                || armorSlot == 7 && unlocalizedName.startsWith("item.leggings")
                || armorSlot == 8 && unlocalizedName.startsWith("item.boots");
    }

}
