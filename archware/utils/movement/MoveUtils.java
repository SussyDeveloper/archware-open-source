package archware.utils.movement;

import archware.Client;
import archware.module.impl.combat.KillAura;
import archware.module.impl.combat.TargetStrafe;
import archware.utils.rotation.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import archware.event.impl.EventMove;
import archware.utils.Wrapper;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class MoveUtils {
    static Minecraft mc = Minecraft.getMinecraft();
    public static final double WALK_SPEED = 0.221;

    public static boolean isMoving() {
        return Minecraft.getMinecraft().thePlayer.movementInput.moveForward != 0F || Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe != 0F;
    }

    public static double getDirection() {
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;

        if(Minecraft.getMinecraft().thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if(Minecraft.getMinecraft().thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if(Minecraft.getMinecraft().thePlayer.moveForward > 0F)
            forward = 0.5F;

        if(Minecraft.getMinecraft().thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if(Minecraft.getMinecraft().thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static void setX(final double x) {
        setPos(x, MoveUtils.mc.thePlayer.posY, MoveUtils.mc.thePlayer.posZ);
    }

    public static void setZ(final double z) {
        setPos(MoveUtils.mc.thePlayer.posX, 0.0, MoveUtils.mc.thePlayer.posZ);
    }

    public static void setY(final double y) {
        MoveUtils.mc.thePlayer.setPosition(MoveUtils.mc.thePlayer.posX, y, MoveUtils.mc.thePlayer.posZ);
    }

    public static void setPos(final double x, final double y, final double z) {
        MoveUtils.mc.thePlayer.setPosition(x, y, z);
    }

    public static Block getBlockAtPos(BlockPos inBlockPos) {
        IBlockState s = mc.theWorld.getBlockState(inBlockPos);
        return s.getBlock();
    }

    public static float getMovementDirection() {
        float forward = Wrapper.getPlayer().movementInput.moveForward;
        float strafe = Wrapper.getPlayer().movementInput.moveStrafe;

        float direction = 0.0f;
        if (forward < 0) {
            direction += 180;
            if (strafe > 0) {
                direction += 45;
            } else if (strafe < 0) {
                direction -= 45;
            }
        } else if (forward > 0) {
            if (strafe > 0) {
                direction -= 45;
            } else if (strafe < 0) {
                direction += 45;
            }
        } else {
            if (strafe > 0) {
                direction -= 90;
            } else if (strafe < 0) {
                direction += 90;
            }
        }

        direction += Wrapper.getPlayer().rotationYaw;

        return MathHelper.wrapAngleTo180_float(direction);
    }

    public static boolean isBlockAbove() {
        for (double height = 0.0D; height <= 1.0D; height += 0.5D) {
            List<AxisAlignedBB> collidingList = Wrapper.getWorld().getCollidingBoundingBoxes(
                    Wrapper.getPlayer(),
                    Wrapper.getPlayer().getEntityBoundingBox().offset(0, height, 0));
            if (!collidingList.isEmpty())
                return true;
        }

        return false;
    }

    public static void setSpeed(EventMove e, double speed) {
        final EntityPlayerSP player = Wrapper.getPlayer();
        final TargetStrafe targetStrafe = new TargetStrafe();
        final KillAura killAura = new KillAura();
        if (Client.moduleManager.getModuleByName("TargetStrafe").isEnabled()) {
            final EntityLivingBase target = killAura.getTarget();
            if (target != null && Client.moduleManager.getModuleByName("KillAura").isEnabled()) {
                if ((Client.moduleManager.getModuleByName("Speed").isEnabled() && TargetStrafe.getSpeedProperty().isEnable())
                        || (Client.moduleManager.getModuleByName("Flight").isEnabled() && TargetStrafe.getFlightProperty().isEnable())) {
                    float dist = Wrapper.getPlayer().getDistanceToEntity(target);
                    double radius = targetStrafe.getRadiusProperty().getValue();
                    setSpeed(e, speed,
                            dist <= radius + 1.0E-4D ? 0 : 1,
                            dist <= radius + 1.0D ? targetStrafe.direction : 0,
                            RotationUtils.getYawToEntity(target, true));
                    return;
                }
            }
        }
        setSpeed(e, speed, player.moveForward, player.moveStrafing, player.rotationYaw);
    }


    public static void setSpeed(EventMove e, double speed, float forward, float strafing, float yaw) {
        boolean reversed = (forward < 0.0F);
        float strafingYaw = 90.0F * ((forward > 0.0F) ? 0.5F : (reversed ? -0.5F : 1.0F));
        if (reversed)
            yaw += 180.0F;
        if (strafing > 0.0F) {
            yaw -= strafingYaw;
        } else if (strafing < 0.0F) {
            yaw += strafingYaw;
        }
        double x = Math.cos(Math.toRadians((yaw + 90.0F)));
        double z = Math.cos(Math.toRadians(yaw));
        e.setX(x * speed);
        e.setZ(z * speed);
    }


    public static void setSpeed(final double moveSpeed) {
        setSpeed(moveSpeed, Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe, Minecraft.getMinecraft().thePlayer.movementInput.moveForward);
    }

    public static void setSpeed(final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            }
            else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0;
            if (forward > 0.0) {
                forward = 1.0;
            }
            else if (forward < 0.0) {
                forward = -1.0;
            }
        }
        if (strafe > 0.0) {
            strafe = 1.0;
        }
        else if (strafe < 0.0) {
            strafe = -1.0;
        }
        final double offsetX = Math.cos(Math.toRadians(yaw + 90.0f));
        final double offsetZ = Math.sin(Math.toRadians(yaw + 90.0f));
        mc.thePlayer.motionX = forward * moveSpeed * offsetX + strafe * moveSpeed * offsetZ;
        mc.thePlayer.motionZ = forward * moveSpeed * offsetZ - strafe * moveSpeed * offsetX;
    }

    public static void strafe(final float speed) {
        if(!isMoving())
            return;

        final double yaw = getDirection();
        Minecraft.getMinecraft().thePlayer.motionX = -Math.sin(yaw) * speed;
        Minecraft.getMinecraft().thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static void strafe() {
        strafe(getSpeed());
    }
    public static float getSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static boolean getOnRealGround(final EntityLivingBase entity, final double y) {
        return !MoveUtils.mc.theWorld.getCollidingBoundingBoxes(MoveUtils.mc.thePlayer, entity.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty();
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static boolean isOnGround() {
        return Wrapper.getPlayer().onGround && Wrapper.getPlayer().isCollidedVertically;
    }

    public static boolean isOverVoid() {
        for (int i = (int)(MoveUtils.mc.thePlayer.posY - 1.0); i > 0; --i) {
            final BlockPos pos = new BlockPos(MoveUtils.mc.thePlayer.posX, i, MoveUtils.mc.thePlayer.posZ);
            if (!(MoveUtils.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInLiquid() {
        return Wrapper.getPlayer().isInWater() || Wrapper.getPlayer().isInLava();
    }

    public static double baseMoveSpeed() {
        return 0.2875;
    }

    public static int getJumpBoostModifier() {
        PotionEffect effect = Wrapper.getPlayer().getActivePotionEffect(Potion.jump.id);
        if (effect != null)
            return effect.getAmplifier() + 1;
        return 0;
    }
}

