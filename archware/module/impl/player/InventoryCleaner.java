package archware.module.impl.player;

import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.event.impl.EventUpdate;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.ModeSetting;
import archware.utils.InventoryUtils;
import archware.utils.packet.PacketUtils;
import net.minecraft.block.*;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.NumberSetting;
import archware.utils.timers.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryCleaner extends Module {

    public static int weaponSlot = 36;

    public static int pickaxeSlot = 42;

    public static int axeSlot = 43;

    public static int shovelSlot = 44;

    NumberSetting delay = new NumberSetting("Delay", 300.0, 0.0, 500.0, 50.0, true);
    BoolSetting inventoryOnly = new BoolSetting("Inv Only", false);
    BoolSetting dropArchery = new BoolSetting("Drop archery", false);
    BoolSetting moveArrows = new BoolSetting("Move arrows", true);
    BoolSetting dropFood = new BoolSetting("Drop food", false);

    NumberSetting slotWeapon = new NumberSetting("Weapon Slot", 1, 1, 9, 1, true);
    NumberSetting slotPick = new NumberSetting("Pickaxe Slot", 2, 1, 9, 1, true);
    NumberSetting slotAxe = new NumberSetting("Axe Slot", 3, 1, 9, 1, true);
    NumberSetting slotShovel = new NumberSetting("Shovel Slot", 4, 1, 9, 1, true);
    NumberSetting slotBow = new NumberSetting("Bow Slot", 5, 1, 9, 1, true);
    NumberSetting slotBlock = new NumberSetting("Block Slot", 6, 1, 9, 1, true);


    private final String[] shitItems = {"tnt", "stick", "egg", "string", "cake", "mushroom", "flint", "compass", "dyePowder", "feather", "bucket", "chest", "snow", "fish", "enchant", "exp", "shears", "anvil", "torch", "seeds", "leather", "reeds", "skull", "record", "snowball", "piston"};
    private final String[] serverItems = {"selector", "tracking compass", "(right click)", "tienda ", "perfil", "salir", "shop", "collectibles", "game", "profil", "lobby", "show all", "hub", "friends only", "cofre", "(click", "teleport", "play", "exit", "hide all", "jeux", "gadget", " (activ", "emote", "amis", "bountique", "choisir", "choose "};

    Stopwatch timer = new Stopwatch();
    boolean isInvOpen;

    public InventoryCleaner() {
        super("InvCleaner", "", Keyboard.KEY_N, Category.PLAYER);
            setDisplayName("InventoryCleaner");
        addSettings(delay, inventoryOnly, dropArchery, moveArrows, dropFood, slotWeapon, slotPick, slotAxe, slotShovel, slotBow, slotBlock);
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventMotion){
            if (e.isPre()) {
                if (stop()) return;
                if (!mc.thePlayer.isUsingItem() && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiIngameMenu)) {
                    long delayTime = (long) delay.getValue();
                    if (timer.elapsed(delayTime)) {
                        Slot slot = mc.thePlayer.inventoryContainer.getSlot(getDesiredSlot(ItemType.WEAPON));
                        if (!slot.getHasStack() || !isBestWeapon(slot.getStack())) {
                            getBestWeapon();
                        }
                    }
                    if (timer.elapsed(delayTime)) getBestPickaxe();
                    if (timer.elapsed(delayTime)) getBestAxe();
                    if (timer.elapsed(delayTime)) getBestShovel();
                    if (timer.elapsed(delayTime)) {
                        for (int i = 9; i < 45; i++) {
                            if (stop()) return;
                            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                            ItemStack is = slot.getStack();
                            if (is != null && shouldDrop(is, i)) {
                                InventoryUtils.drop(i);
                                timer.reset();
                                break;
                            }
                        }
                    }
                    if (timer.elapsed(delayTime)) swapBlocks();
                    if (timer.elapsed(delayTime)) getBestBow();
                    if (timer.elapsed(delayTime)) moveArrows();
                }
            }
        }
    }

    public static float getDamageScore(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0;

        float damage = 0;
        Item item = stack.getItem();

        if (item instanceof ItemSword) {
            damage += ((ItemSword) item).getDamageVsEntity();
        } else if (item instanceof ItemTool) {
            damage += item.getMaxDamage();
        }

        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F +
                EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.1F;

        return damage;
    }

    public static float getProtScore(ItemStack stack) {
        float prot = 0;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            prot += armor.damageReduceAmount
                    + ((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 25.F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100F;
        }
        return prot;
    }

    private int getDesiredSlot(ItemType tool) {
        int slot = 36;
        switch (tool) {
            case WEAPON:
                slot = (int) slotWeapon.getValue();
                break;
            case PICKAXE:
                slot = (int) slotPick.getValue();
                break;
            case AXE:
                slot = (int) slotAxe.getValue();
                break;
            case SHOVEL:
                slot = (int) slotShovel.getValue();
                break;
            case BLOCK:
                slot = (int) slotBlock.getValue();
                break;
            case BOW:
                slot = (int) slotBow.getValue();
                break;
        }
        return slot + 35;
    }

    private boolean isBestWeapon(ItemStack is) {
        float damage = getDamageScore(is);
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is2 = slot.getStack();
                if (getDamageScore(is2) > damage && is2.getItem() instanceof ItemSword) {
                    return false;
                }
            }
        }
        return is.getItem() instanceof ItemSword;
    }

    private void getBestWeapon() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (isBestWeapon(is) && getDamageScore(is) > 0 && is.getItem() instanceof ItemSword) {
                    swap(i, getDesiredSlot(ItemType.WEAPON) - 36);
                    break;
                }
            }
        }
    }

    private boolean shouldDrop(ItemStack stack, int slot) {
        String stackName = stack.getDisplayName().toLowerCase();
        Item item = stack.getItem();
        String ulName = item.getUnlocalizedName();
        if (Arrays.stream(serverItems).anyMatch(stackName::contains)) return false;

        if (item instanceof ItemBlock) {
            return !BlockUtils.isValidBlock(((ItemBlock) item).getBlock(), true);
        }

        int weaponSlot = getDesiredSlot(ItemType.WEAPON);
        int pickaxeSlot = getDesiredSlot(ItemType.PICKAXE);
        int axeSlot = getDesiredSlot(ItemType.AXE);
        int shovelSlot = getDesiredSlot(ItemType.SHOVEL);

        if ((slot != weaponSlot || !isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack()))
                && (slot != pickaxeSlot || !isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack()))
                && (slot != axeSlot || !isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack()))
                && (slot != shovelSlot || !isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack()))) {
            if (item instanceof ItemArmor) {
                for (int type = 1; type < 5; type++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                        if (isBestArmor(is, type)) {
                            continue;
                        }
                    }
                    if (isBestArmor(stack, type)) {
                        return false;
                    }
                }
            }

            if ((item == Items.wheat) || item == Items.spawn_egg
                    || (item instanceof ItemFood && dropFood.isEnable() && !(item instanceof ItemAppleGold))
                    || (item instanceof ItemPotion && isShitPotion(stack))) {
                return true;
            } else if (!(item instanceof ItemSword) && !(item instanceof ItemTool) && !(item instanceof ItemHoe) && !(item instanceof ItemArmor)) {
                if (dropArchery.isEnable() && (item instanceof ItemBow || item == Items.arrow)) {
                    return true;
                } else {
                    return item instanceof ItemGlassBottle || Arrays.stream(shitItems).anyMatch(ulName::contains);
                }
            }
            return true;
        }

        return false;
    }

    private void getBestPickaxe() {
        for (int i = 9; i < 45; ++i) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestPickaxe(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.PICKAXE);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestPickaxe(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                    }
                }
            }
        }
    }

    private void getBestAxe() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestAxe(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.AXE);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestAxe(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                        timer.reset();
                    }
                }
            }
        }
    }

    private void getBestShovel() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestShovel(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.SHOVEL);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestShovel(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                        timer.reset();
                    }
                }
            }
        }
    }

    private void getBestBow() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                String stackName = is.getDisplayName().toLowerCase();
                if (Arrays.stream(serverItems).anyMatch(stackName::contains) || !(is.getItem() instanceof ItemBow))
                    continue;
                if (isBestBow(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.BOW);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestBow(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                    }
                }
            }
        }
    }

    private void moveArrows() {
        if (dropArchery.isEnable() || !moveArrows.isEnable()) return;
        for (int i = 36; i < 45; i++) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (is != null && is.getItem() == Items.arrow) {
                for (int j = 0; j < 36; j++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(j).getStack() == null) {
                        fakeOpen();
                        InventoryUtils.click(i, 0, true);
                        fakeClose();
                        timer.reset();
                        break;
                    }
                }
            }
        }
    }

    private int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack is = slot.getStack();
            if (is != null && is.getItem() instanceof ItemBlock && is.stackSize > stack && Arrays.stream(serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) {
                stack = is.stackSize;
                biggestSlot = i;
            }
        }
        return biggestSlot;
    }

    private void swapBlocks() {
        int mostBlocksSlot = getMostBlocks();
        int desiredSlot = getDesiredSlot(ItemType.BLOCK);
        if (mostBlocksSlot != -1 && mostBlocksSlot != desiredSlot) {
            // only switch if the hotbar slot doesn't already have blocks of the same quantity to prevent an inf loop
            Slot dss = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
            ItemStack dsis = dss.getStack();
            if (!(dsis != null && dsis.getItem() instanceof ItemBlock && dsis.stackSize >= mc.thePlayer.inventoryContainer.getSlot(mostBlocksSlot).getStack().stackSize && Arrays.stream(serverItems).noneMatch(dsis.getDisplayName().toLowerCase()::contains))) {
                swap(mostBlocksSlot, desiredSlot - 36);
            }
        }
    }

    private boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe)) {
            return false;
        } else {
            float value = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getItem() instanceof ItemPickaxe && getToolScore(is) > value) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestShovel(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSpade)) {
            return false;
        } else {
            float score = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getItem() instanceof ItemSpade && getToolScore(is) > score) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestAxe(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemAxe)) {
            return false;
        } else {
            float value = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (getToolScore(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestBow(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemBow)) {
            return false;
        } else {
            float value = getPowerLevel(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (getPowerLevel(is) > value && is.getItem() instanceof ItemBow && !isBestWeapon(stack)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private float getPowerLevel(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemBow) {
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) * 0.5F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 0.1F;
        }
        return score;
    }

    private float getToolScore(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
            String name = item.getUnlocalizedName().toLowerCase();
            if (item instanceof ItemPickaxe) {
                score = tool.getStrVsBlock(stack, Blocks.stone) - (name.contains("gold") ? 5 : 0);
            } else if (item instanceof ItemSpade) {
                score = tool.getStrVsBlock(stack, Blocks.dirt) - (name.contains("gold") ? 5 : 0);
            } else {
                if (!(item instanceof ItemAxe)) return 1;
                score = tool.getStrVsBlock(stack, Blocks.log) - (name.contains("gold") ? 5 : 0);
            }
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100F;
        }
        return score;
    }

    private boolean isShitPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion pot = (ItemPotion) stack.getItem();
            if (pot.getEffects(stack) == null) return true;
            for (PotionEffect effect : pot.getEffects(stack)) {
                if (effect.getPotionID() == Potion.moveSlowdown.getId()
                        || effect.getPotionID() == Potion.weakness.getId()
                        || effect.getPotionID() == Potion.poison.getId()
                        || effect.getPotionID() == Potion.harm.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBestArmor(ItemStack stack, int type) {
        String typeStr = "";
        switch (type) {
            case 1:
                typeStr = "helmet";
                break;
            case 2:
                typeStr = "chestplate";
                break;
            case 3:
                typeStr = "leggings";
                break;
            case 4:
                typeStr = "boots";
                break;
        }
        if (stack.getUnlocalizedName().contains(typeStr)) {
            float prot = getProtScore(stack);
            for (int i = 5; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getUnlocalizedName().contains(typeStr) && getProtScore(is) > prot) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void fakeOpen() {
        if (!isInvOpen) {
            timer.reset();
            if (!inventoryOnly.isEnable())
                PacketUtils.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            isInvOpen = true;
        }
    }

    private void fakeClose() {
        if (isInvOpen) {
            if (!inventoryOnly.isEnable())
                PacketUtils.sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            isInvOpen = false;
        }
    }

    private void swap(int slot, int hSlot) {
        fakeOpen();
        InventoryUtils.swap(slot, hSlot);
        fakeClose();
        timer.reset();
    }

    private boolean stop() {
        return (inventoryOnly.isEnable() && !(mc.currentScreen instanceof GuiInventory)) /*|| (onlyWhileNotMoving.isEnabled() && MovementUtils.isMoving()*/;
    }

    private enum ItemType {
        WEAPON, PICKAXE, AXE, SHOVEL, BLOCK, BOW
    }

    private static class BlockUtils{
        public boolean isValidBlock(BlockPos pos) {
            return isValidBlock(mc.theWorld.getBlockState(pos).getBlock(), false);
        }

        public static boolean isValidBlock(Block block, boolean placing) {
            if (block instanceof BlockCarpet
                    || block instanceof BlockSnow
                    || block instanceof BlockContainer
                    || block instanceof BlockBasePressurePlate
                    || block.getMaterial().isLiquid()) {
                return false;
            }
            if (placing && (block instanceof BlockSlab
                    || block instanceof BlockStairs
                    || block instanceof BlockLadder
                    || block instanceof BlockStainedGlassPane
                    || block instanceof BlockWall
                    || block instanceof BlockWeb
                    || block instanceof BlockCactus
                    || block instanceof BlockFalling
                    || block == Blocks.glass_pane
                    || block == Blocks.iron_bars)) {
                return false;
            }
            return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isFullBlock());
        }

        public boolean isInLiquid() {
            if (mc.thePlayer == null) return false;
            for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
                for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                    BlockPos pos = new BlockPos(x, (int) mc.thePlayer.getEntityBoundingBox().minY, z);
                    Block block = mc.theWorld.getBlockState(pos).getBlock();
                    if (block != null && !(block instanceof BlockAir)) {
                        return block instanceof BlockLiquid;
                    }
                }
            }
            return false;
        }

        public boolean isOnLiquid() {
            if (mc.thePlayer == null) return false;
            AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox();
            if (boundingBox != null) {
                boundingBox = boundingBox.contract(0.01D, 0.0D, 0.01D).offset(0.0D, -0.01D, 0.0D);
                boolean onLiquid = false;
                int y = (int) boundingBox.minY;

                for (int x = MathHelper.floor_double(boundingBox.minX); x < MathHelper.floor_double(boundingBox.maxX + 1.0D); ++x) {
                    for (int z = MathHelper.floor_double(boundingBox.minZ); z < MathHelper.floor_double(boundingBox.maxZ + 1.0D); ++z) {
                        Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                        if (block != Blocks.air) {
                            if (!(block instanceof BlockLiquid)) return false;
                            onLiquid = true;
                        }
                    }
                }

                return onLiquid;
            }
            return false;
        }
    }

}
