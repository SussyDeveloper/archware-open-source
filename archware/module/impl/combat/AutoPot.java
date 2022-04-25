package archware.module.impl.combat;

import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.InventoryUtils;
import archware.utils.Wrapper;
import archware.utils.movement.MoveUtils;
import archware.utils.timers.Stopwatch;

public class AutoPot extends Module {

    private static final byte HEALTH_BELOW = 1;
    private static final byte BETTER_THAN_CURRENT = 2;

    private static final PotionType[] VALID_POTIONS = {PotionType.HEALTH, PotionType.REGEN, PotionType.SPEED};

    private final C08PacketPlayerBlockPlacement THROW_POTION_PACKET = new C08PacketPlayerBlockPlacement(
            new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f);
    ModeSetting mode = new ModeSetting("Mode", "Jump", "Jump", "Floor", "Jump Only");
    NumberSetting healthProperty = new NumberSetting("Health", 6.0, 1.0, 10.0, 0.5, true);
    NumberSetting potionAndSoupSlotProperty = new NumberSetting("Slot", 7.0, 1.0, 9.0, 1, true);
    NumberSetting delayProperty = new NumberSetting("Delay", 700, 0, 1000, 50, true);

    private final Stopwatch interactionTimer = new Stopwatch();
    private int prevSlot;
    private boolean potting;
    private String potionCounter;

    private boolean jumpNextTick;

    public AutoPot() {
        super("AutoPot", "", 0, Category.COMBAT);
        addSettings(healthProperty, potionAndSoupSlotProperty, delayProperty, mode);
    }

    @Override
    public void onEvent(Event e) {
        super.onEvent(e);
        if(e instanceof EventUpdate){
            setDisplayName("AutoPot " + ChatFormatting.GRAY + mode.getSelected());
            if(mode.is("Jump Only")){
                NotificationManager.queue("Mode changed!", "Sorry, but Jump Only mode is in development", NotificationType.INFO, 3000);
                mode.cycle();
            }
        }

        if(e instanceof EventMotion){
            EventMotion event = (EventMotion) e;
            if (Wrapper.getMinecraft().currentScreen != null)
                return;
//            if (ModuleManager.getInstance(Scaffold.class).isEnabled())
//                return;
            if (e.isPre()) {
                if (jumpNextTick) {
                    Wrapper.getPlayer().setPosition(
                            Wrapper.getPlayer().posX,
                            Wrapper.getPlayer().posY + 1.2492F,
                            Wrapper.getPlayer().posZ);
                    event.setY(event.getY() + 1.2492F);
                    jumpNextTick = false;
                }

                potionCounter = Integer.toString(getValidPotionsInInv());
                if (interactionTimer.elapsed((long) delayProperty.getValue())) {
                    for (int slot = 9; slot < 45; slot++) {
                        ItemStack stack = Wrapper.getStackInSlot(slot);
                        if (stack != null && stack.getItem() instanceof ItemPotion &&
                                ItemPotion.isSplash(stack.getMetadata()) && InventoryUtils.isBuffPotion(stack)) {
                            ItemPotion itemPotion = (ItemPotion) stack.getItem();
                            boolean validEffects = false;
                            // Use ItemPotion#getEffects(int) Note: The int parameter, the method is considerably faster
                            for (PotionEffect effect : itemPotion.getEffects(stack.getMetadata())) {
                                for (PotionType potionType : VALID_POTIONS) {
                                    if (potionType.potionId == effect.getPotionID()) {
                                        validEffects = true;
                                        if (hasFlag(potionType.requirementFlags, HEALTH_BELOW))
                                            validEffects = Wrapper.getPlayer().getHealth() < healthProperty.getValue() * 2.0F;
                                        boolean orIsLesserPresent = hasFlag(potionType.requirementFlags, BETTER_THAN_CURRENT);
                                        PotionEffect activePotionEffect = Wrapper.getPlayer().getActivePotionEffect(potionType.potionId);
                                        if (orIsLesserPresent)
                                            if (activePotionEffect != null)
                                                validEffects &= activePotionEffect.getAmplifier() < effect.getAmplifier();
                                    }
                                }

                            }

                            if (validEffects) {
                                if (MoveUtils.isOverVoid())
                                    return;

                                prevSlot = Wrapper.getPlayer().inventory.currentItem;

                                double xDist = Wrapper.getPlayer().posX - Wrapper.getPlayer().lastTickPosX;
                                double zDist = Wrapper.getPlayer().posZ - Wrapper.getPlayer().lastTickPosZ;

                                double speed = Math.sqrt(xDist * xDist + zDist * zDist);

                                boolean shouldPredict = speed > 0.38D;
                                boolean shouldJump = speed < MoveUtils.WALK_SPEED;
                                boolean onGround = MoveUtils.isOnGround();

                                if (shouldJump && onGround && !MoveUtils.isBlockAbove() && MoveUtils.getJumpBoostModifier() == 0) {
                                    Wrapper.getPlayer().motionX = 0.0D;
                                    Wrapper.getPlayer().motionZ = 0.0D;
                                    if(mode.is("Jump")) {
                                        event.setPitch(-90.0F);
                                    }else{
                                        event.setPitch(90.0F);
                                    }
//                                jumpNextTick = true;
                                    if(mode.is("Jump")) {
                                        Wrapper.getPlayer().jump();
                                    }
                                } else if (shouldPredict || onGround) {
                                    event.setYaw(MoveUtils.getMovementDirection());
                                    event.setPitch(shouldPredict ? 0.0F : 45.0F);
                                } else return;

                                final int potSlot;
//                                KillAura.getInstance().waitTicks = 2;
                                if (slot >= 36) { // In hotbar
                                    potSlot = slot - 36;
                                } else { // Get it from inventory
                                    int potionSlot = (int) (potionAndSoupSlotProperty.getValue() - 1);
                                    InventoryUtils.windowClick(slot, potionSlot,
                                            InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                                    potSlot = potionSlot;
                                }
                                Wrapper.sendPacketDirect(new C09PacketHeldItemChange(potSlot));
                                potting = true;
                                break;
                            }
                        }
                    }
                }
            } else if (potting && prevSlot != -1) {
                Wrapper.sendPacketDirect(THROW_POTION_PACKET);
                Wrapper.sendPacketDirect(new C09PacketHeldItemChange(prevSlot));
                interactionTimer.reset();
                prevSlot = -1;
                potting = false;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        potionCounter = "0";
        prevSlot = -1;
        potting = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private int getValidPotionsInInv() {
        int count = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = Wrapper.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ItemPotion &&
                    ItemPotion.isSplash(stack.getMetadata()) && InventoryUtils.isBuffPotion(stack)) {
                ItemPotion itemPotion = (ItemPotion) stack.getItem();
                for (PotionEffect effect : itemPotion.getEffects(stack.getMetadata())) {
                    boolean breakOuter = false;
                    for (PotionType type : VALID_POTIONS) {
                        if (type.potionId == effect.getPotionID()) {
                            count++;
                            breakOuter = true;
                            break;
                        }
                    }
                    if (breakOuter) break;
                }
            }
        }

        return count;
    }

    private boolean hasFlag(int flags, int flagToCheck) {
        return (flags & flagToCheck) == flagToCheck;
    }

    private enum Items {
        HEADS, POTIONS, SOUPS
    }

    private enum PotionType {
        SPEED(Potion.moveSpeed.id, BETTER_THAN_CURRENT),
        REGEN(Potion.regeneration.id, BETTER_THAN_CURRENT | HEALTH_BELOW),
        HEALTH(Potion.heal.id, HEALTH_BELOW);

        private final int potionId;
        private final int requirementFlags;

        PotionType(int potionId, int requirementFlags) {
            this.potionId = potionId;
            this.requirementFlags = requirementFlags;
        }
    }
}
