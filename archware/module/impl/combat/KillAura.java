package archware.module.impl.combat;

import archware.Client;
import archware.event.impl.Event3D;
import archware.utils.player.PlayerUtil;
import archware.utils.render.RenderUtils;
import archware.utils.rotation.RotationUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.lwjgl.input.Keyboard;
import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.timers.Stopwatch;

import java.awt.*;
import java.util.SplittableRandom;

public class KillAura extends Module {

    ModeSetting mode = new ModeSetting("Mode", "Switch", "Single", "Switch");
    ModeSetting targetesps = new ModeSetting("TargetESP", "None", "Normal", "Box", "None");
    BoolSetting autoblock = new BoolSetting("AutoBlock", true);
    BoolSetting players = new BoolSetting("Players", true);
    BoolSetting others = new BoolSetting("Others", false);
    BoolSetting teams = new BoolSetting("Teams", true);
    NumberSetting aps = new NumberSetting("APS", 13, 5, 15, 1, true);
    NumberSetting range = new NumberSetting("Range", 4.2, 1.0, 8.0, 0.1, false);
    public static EntityLivingBase target;
    public Stopwatch timer = new Stopwatch();
    boolean canblock;
    public float yaw, pitch;
    private static final Stopwatch switchTimer = new Stopwatch();
    private static final SplittableRandom random = new SplittableRandom();
    int targetNumber;

    public KillAura() {
        super("KillAura", "It attacks entities", Keyboard.KEY_R, Category.COMBAT);
        addSettings(mode, targetesps, autoblock, players, teams, others, range, aps);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventUpdate){
            this.setDisplayName("KillAura " + ChatFormatting.GRAY + mode.getSelected());
        }

        if(event instanceof EventMotion){
            if(event.isPre()) {
                canblock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.getHeldItem().getItem() != null;
                yaw = mc.thePlayer.rotationYaw;
                pitch = mc.thePlayer.rotationPitch;

                Object[] possibleTargets = mc.theWorld.loadedEntityList.stream().filter(this::isValid).toArray();
                if (possibleTargets.length > 0 && !(possibleTargets[0] instanceof EntityLargeFireball)) {
                    if ((target == null || !isValid(target))) {
                        target = (EntityLivingBase) possibleTargets[0];
                    }
                }

                if (target == null) return;
                float[] facing = rotations(target);

                if (isValid(target)) {
                    mc.thePlayer.rotationYaw = facing[0];
                    mc.thePlayer.rotationPitch = facing[1];
                    //Client Side Rotations
                    mc.thePlayer.rotationYawHead = facing[0];
                    mc.thePlayer.renderYawOffset = facing[0];
                    mc.thePlayer.rotationPitchHead = facing[1];

                    if(autoblock.isEnable()) {
                        block();
                    }

                    if (!timer.elapsed((long) (1000 / aps.getValue()))) return;

                    mc.thePlayer.swingItem();
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));

                    if (mode.is("Switch") && possibleTargets.length > 0) {
                        targetNumber = random.nextInt(possibleTargets.length);
                        target = (EntityLivingBase) possibleTargets[targetNumber];
                    }
                }

                timer.reset();
                switchTimer.reset();
            }

            if(event.isPost()){
                mc.thePlayer.rotationYaw = yaw;
                mc.thePlayer.rotationPitch = pitch;

                if (target == null)
                    return;

                if (!timer.elapsed((long) (1000 / aps.getValue()))) return;
            }
        }

        if(event instanceof Event3D) {
            Color color = new Color(200, 255, 100, 75);
            if (!Client.moduleManager.getModuleByName("Scaffold").isEnabled()) {
                if (targetesps.is("Box") && getTarget() != null)
                    RenderUtils.drawPlatform((Entity) getTarget(), ((getTarget()).hurtTime > 3) ? color : new Color(235, 40, 40, 75));
                if (targetesps.is("Normal") && getTarget() != null)
                    RenderUtils.drawAuraMark((Entity) getTarget(), ((getTarget()).hurtTime > 3) ? color : new Color(235, 40, 40, 75));
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


    public float[] rotations(Entity e) {
        double deltaX = e.boundingBox.minX + (e.boundingBox.maxX - e.boundingBox.minX + 0.1) - mc.thePlayer.posX, deltaY = e.posY - 4.25 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), deltaZ = e.boundingBox.minZ + (e.boundingBox.maxX - e.boundingBox.minX) - mc.thePlayer.posZ, distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)), pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));
        final double v = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if (deltaX < 0 && deltaZ < 0) yaw = (float) (90 + v);
        else if (deltaX > 0 && deltaZ < 0) yaw = (float) (-90 + v);
        return new float[]{yaw, pitch};
    }

    public EntityLivingBase getTarget() {
        EntityLivingBase target = null;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entity2 = (EntityLivingBase) entity;
                if (mc.thePlayer.getDistanceToEntity(entity) <= range.getValue() && isValid(entity2)) {
                    target = entity2;
                }
            }
        }
        return target;
    }

    public boolean isValid(Entity entity) {
        if (entity != null) {
            if (entity.getDistanceToEntity(mc.thePlayer) <= range.getValue()) {
                if (entity instanceof EntityLivingBase) {
                    if (entity != mc.thePlayer && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0) {
                        if (players.isEnable() && (entity instanceof EntityOtherPlayerMP)) {
                            return true;
                        }
                        if (others.isEnable() && (entity instanceof EntityAnimal || entity instanceof EntityMob || entity instanceof EntityVillager)) {
                            return true;
                        }
                        if(!teams.isEnable() && !RotationUtils.isOnSameTeam((EntityLivingBase) entity)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void block(){
        if(canblock){
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
        }
    }

    public boolean Canblock() {
        return canblock;
    }
}
