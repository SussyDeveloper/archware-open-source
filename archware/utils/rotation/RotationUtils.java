package archware.utils.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.MathHelper;
import archware.utils.Wrapper;

import static archware.module.Module.mc;

public class RotationUtils {
    public static float[] getRotationsToEntity(Entity entity) {
        final EntityPlayerSP player = Wrapper.getPlayer();
        double xDist = entity.posX - player.posX;
        double zDist = entity.posZ - player.posZ;

        double entEyeHeight = entity.getEyeHeight();
        double yDist = ((entity.posY + entEyeHeight) - Math.min(Math.max(entity.posY - player.posY, 0), entEyeHeight)) -
                (player.posY + player.getEyeHeight());
        double fDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
        float rotationYaw = Wrapper.getPlayer().rotationYaw;
        float var1 = (float) (Math.atan2(zDist, xDist) * 180.0D / Math.PI) - 90.0F;

        float yaw = rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
        float rotationPitch = Wrapper.getPlayer().rotationPitch;

        float var2 = (float) (-(Math.atan2(yDist, fDist) * 180.0D / Math.PI));
        float pitch = rotationPitch + MathHelper.wrapAngleTo180_float(var2 - rotationPitch);

        return new float[]{yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f)};
    }

    public static float getYawToEntity(Entity entity, boolean useOldPos) {
        final EntityPlayerSP player = Wrapper.getPlayer();
        double xDist = (useOldPos ? entity.prevPosX : entity.posX) -
                (useOldPos ? player.prevPosX : player.posX);
        double zDist = (useOldPos ? entity.prevPosZ : entity.posZ) -
                (useOldPos ? player.prevPosZ : player.posZ);
        float rotationYaw = useOldPos ? Wrapper.getPlayer().prevRotationYaw : Wrapper.getPlayer().rotationYaw;
        float var1 = (float) (Math.atan2(zDist, xDist) * 180.0D / Math.PI) - 90.0F;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }

    public static float clampRotation() {
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float n = 1.0f;
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward < 0.0f) {
            rotationYaw += 180.0f;
            n = -0.5f;
        }
        else if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward > 0.0f) {
            n = 0.5f;
        }
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe > 0.0f) {
            rotationYaw -= 90.0f * n;
        }
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe < 0.0f) {
            rotationYaw += 90.0f * n;
        }
        return rotationYaw * 0.017453292f;
    }

    public static float[] getFacingRotations2(final int paramInt1, final double d, final int paramInt3) {
        final EntitySnowball localEntityPig = new EntitySnowball(Minecraft.getMinecraft().theWorld);
        localEntityPig.posX = paramInt1 + 0.5;
        localEntityPig.posY = d + 0.5;
        localEntityPig.posZ = paramInt3 + 0.5;
        return getRotationsNeeded(localEntityPig);
    }

    public static float[] getRotationsNeeded(final Entity entity) {
        if (entity == null) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        final double xSize = entity.posX - mc.thePlayer.posX;
        final double ySize = entity.posY + entity.getEyeHeight() / 2 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        final double zSize = entity.posZ - mc.thePlayer.posZ;
        final double theta = MathHelper.sqrt_double(xSize * xSize + zSize * zSize);
        final float yaw = (float) (Math.atan2(zSize, xSize) * 180 / Math.PI) - 90;
        final float pitch = (float) (-(Math.atan2(ySize, theta) * 180 / Math.PI));
        return new float[]{(mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw)) % 360, (mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)) % 360.0f};
    }

    public static boolean isOnSameTeam(EntityLivingBase entity) {
        if (entity.getTeam() != null && mc.thePlayer.getTeam() != null) {
            char c1 = entity.getDisplayName().getFormattedText().charAt(1);
            char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }
}
