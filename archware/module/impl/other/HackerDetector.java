package archware.module.impl.other;

import com.mojang.realmsclient.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import archware.event.Event;
import archware.event.impl.EventMotion;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.BoolSetting;
import archware.ui.notification.NotificationManager;
import archware.ui.notification.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class HackerDetector extends Module {

    private final List<Pair<EntityPlayer, String>> data = new ArrayList<>();
    public static final ArrayList<EntityPlayer> hackers = new ArrayList<>();
    private final ArrayList<String> hacker = new ArrayList<>();
    BoolSetting speed = new BoolSetting("Speed", true);
    BoolSetting fly = new BoolSetting("Flight", true);
    BoolSetting noslow = new BoolSetting("No Slow", true);
    BoolSetting velocity = new BoolSetting("Velocity", true);
    double speedvl;
    double noslowvl;
    double NoKBvl;

    public HackerDetector() {
        super("HackerDetect", "", 0, Category.OTHER);
        addSettings(speed, noslow, fly);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventMotion) {
            if (mc.thePlayer.ticksExisted <= 105) {
                hackers.clear();
                return;
            }
            if (mc.theWorld == null)
                return;
            for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                if (!(entity instanceof EntityPlayer))
                    continue;
                if (entity instanceof EntityOtherPlayerMP) {
                    EntityOtherPlayerMP entityOtherPlayerMP = (EntityOtherPlayerMP) entity;
                    if (speed.isEnable() && getSpeed((EntityPlayer)entityOtherPlayerMP) > getBaseMoveSpeed() + 0.85D && !entityOtherPlayerMP.onGround && !isInLiquid((Entity)entityOtherPlayerMP))
                        informPlayer((EntityPlayer)entityOtherPlayerMP, "Speed");
                }
            }
            for (Entity entity : mc.theWorld.playerEntities) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player instanceof net.minecraft.client.entity.EntityPlayerSP)
                    continue;
                if (player == mc.thePlayer || player.ticksExisted < 105 || hackers.contains(player) || player.capabilities.isFlying || player.capabilities.isCreativeMode)
                    continue;
                double playerSpeed = getBPS((Entity)player);
                double xDif = player.posX - player.prevPosX;
                double zDif = player.posZ - player.prevPosZ;
                double lastDist = Math.sqrt(xDif * xDif + zDif * zDif) * 20.0D;
                if (speed.isEnable() && Math.round(lastDist) > 15L) {
                    this.speedvl++;
                    if (this.speedvl >= 150.0D) {
                        informPlayer(player, "Speed");
                        speedvl = 0.0D;
                        hackers.add(player);
                    }
                }

                if (noslow.isEnable() && player.isBlocking() && SpeedBs((Entity)player) >= 6.0D) {
                    this.noslowvl++;
                    if (this.noslowvl >= 30.0D) {
                        informPlayer(player, "NoSlowDown");
                        this.noslowvl = 0.0D;
                        hackers.add(player);
                    }
                }

                if (velocity.isEnable()) {
                    if (player.hurtResistantTime > 6 && player.hurtResistantTime < 12 && player.lastTickPosX == player.posX && player.posZ == player.lastTickPosZ &&
                            !mc.theWorld.checkBlockCollision(player.getEntityBoundingBox().expand(0.05D, 0.0D, 0.05D))) {
                        this.NoKBvl++;
                        if (this.NoKBvl >= 50.0D) {
                            informPlayer(player, "Velocity");
                            this.NoKBvl = 0.0D;
                            hackers.add(player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        hackers.clear();
        this.data.clear();
        this.hacker.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        hackers.clear();
    }

    public static boolean isHacker(EntityLivingBase ent) {
        for (EntityPlayer hacker : hackers) {
            if (ent.getName().equals(hacker.getName()))
                return true;
        }
        return false;
    }

    private boolean checkGround(double y) {
        return (y % 0.015625D == 0.0D);
    }

    public static double getBPS(Entity entityIn) {
        double xDist = entityIn.posX - entityIn.prevPosX;
        double zDist = entityIn.posZ - entityIn.prevPosZ;
        double bps = Math.sqrt(xDist * xDist + zDist * zDist) * 20.0D;
        return (int)bps + bps - (int)bps;
    }

    public static double SpeedBs(Entity entity) {
        double xDif = entity.posX - entity.prevPosX;
        double zDif = entity.posZ - entity.prevPosZ;
        double lastDist = Math.sqrt(xDif * xDif + zDif * zDif) * 20.0D;
        return Math.round(lastDist);
    }

    public static float[] getFacePosEntityRemote(EntityLivingBase facing, Entity en) {
        if (en == null)
            return new float[] { facing.rotationYawHead, facing.rotationPitch };
        return getFacePosRemote(new Vec3(facing.posX, facing.posY + en.getEyeHeight(), facing.posZ), new Vec3(en.posX, en.posY + en.getEyeHeight(), en.posZ));
    }

    private static float[] getFacePosRemote(Vec3 src, Vec3 dest) {
        double diffX = dest.xCoord - src.xCoord;
        double diffY = dest.yCoord - src.yCoord;
        double diffZ = dest.zCoord - src.zCoord;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(-Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] { MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch) };
    }

    private void informPlayer(EntityPlayer player, String hakk) {
        for (Pair<EntityPlayer, String> pair : this.data) {
            if (pair.first() == player && ((String)pair.second()).equalsIgnoreCase(hakk))
                return;
        }
        NotificationManager.queue("Hacker Detected!", player.getName() + " maybe is using " + hakk, NotificationType.WARNING, 5000);
        this.data.add(Pair.of(player, hakk));
    }

    private double getSpeed(EntityPlayer player) {
        return Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2875D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
            baseSpeed *= 1.0D + 0.2D * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        return baseSpeed;
    }

    private boolean isInLiquid(Entity e) {
        for (int x = MathHelper.floor_double((e.getEntityBoundingBox()).minY); x < MathHelper.floor_double((e.getEntityBoundingBox()).maxX) + 1; x++) {
            for (int z = MathHelper.floor_double((e.getEntityBoundingBox()).minZ); z < MathHelper.floor_double((e.getEntityBoundingBox()).maxZ) + 1; z++) {
                BlockPos pos = new BlockPos(x, (int)(e.getEntityBoundingBox()).minY, z);
                Block block = (Minecraft.getMinecraft()).theWorld.getBlockState(pos).getBlock();
                if (block != null && !(block instanceof net.minecraft.block.BlockAir))
                    return block instanceof net.minecraft.block.BlockLiquid;
            }
        }
        return false;
    }
}
