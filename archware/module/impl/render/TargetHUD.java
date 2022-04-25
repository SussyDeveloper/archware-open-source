package archware.module.impl.render;

import archware.utils.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import archware.Client;
import archware.event.Event;
import archware.event.impl.Event2D;
import archware.module.Category;
import archware.module.Module;
import archware.module.impl.combat.KillAura;
import archware.module.settings.impl.ModeSetting;
import archware.utils.ColorManager;
import archware.utils.MathUtils;
import archware.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TargetHUD extends Module {

    FontManager tahoma = new FontManager("tahoma", Font.BOLD, 16, 1,1);

    ModeSetting mode = new ModeSetting("Mode", "Archware", "Archware", "Exhibition", "Exhibition2");

    double animHealth = 1;
    double width1;
    int drawwidth;

    public TargetHUD() {
        super("TargetHUD", "", 0, Category.RENDER);
        addSettings(mode);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof Event2D){
            ScaledResolution scaledRes = new ScaledResolution(mc);
            float width = scaledRes.getScaledWidth();
            float height = scaledRes.getScaledHeight();

            KillAura aura = new KillAura();
            EntityLivingBase target = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? (EntityLivingBase)mc.thePlayer : aura.getTarget();
            if(target != null && Client.moduleManager.getModuleByName("KillAura").isEnabled()){
                switch (mode.getSelected()){
                    case "Archware":{
                        drawwidth =
                                (int) ((mc.fontRendererObj.getStringWidth(target.getName()) > 50) ? (135.0F + mc.fontRendererObj.getStringWidth(target.getName()) - 50.0F) : 135.0F);
                        width1 = drawwidth - 28;
                        GL11.glPushMatrix();
                        GL11.glTranslated(width / 2 + 10, height / 2 + 30, 0);
                        RenderUtils.drawRoundedRect(0, 0, drawwidth, 55, 5,new Color(0x94000000, true).getRGB());
                        RenderUtils.drawRoundedRect(5, 5, 45, 45, 5,new Color(0x5E4F4F4F, true).getRGB());
                        mc.fontRendererObj.drawStringWithShadow(target.getName(), 55, 6, -1);
                        GuiInventory.drawEntityOnScreen(27, 48, 20, target.rotationYaw, target.rotationPitch, target);
                        Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
                        float[] fractions = { 0.0F, 0.5F, 1.0F };
                        float health = target.getHealth();
                        float totalHealth = target.getHealth() + target.getAbsorptionAmount();
                        float progress = health / target.getMaxHealth();
                        Color customColor = (health >= 0.0F) ? ColorManager.blendColors(fractions, colors, progress).brighter() : Color.RED;
                        double width1 = 0.0D;
                        width1 = ColorManager.getIncremental(width1, 5.0D);
                        if (width1 < 50.0D)
                            width1 = 50.0D;
                        double healthLocation = width1 * progress;
                        RenderUtils.rectangle(54.5D, 15.3D, 53.0D + healthLocation + 0.5D, 18.5D, customColor.getRGB());
                        RenderUtils.drawRectBordered(54.0D, 15D, 54.0D + width1, 19D, 0.5D, ColorManager.getColor(0, 0), ColorManager.getColor(0));
                        drawArmor(target,40, 35);
                        GL11.glScaled(0.5, 0.5,0.5);
                        double hp = (int) (target.getHealth() + target.getAbsorptionAmount());
                        double distance = mc.thePlayer.getDistanceToEntity(target);
                        String stringdis = "Distance: " + (int) distance;
                        String stringhp = "HP: " + hp;
                        mc.fontRendererObj.drawStringWithShadow(stringhp, 110, 40, -1);
                        mc.fontRendererObj.drawStringWithShadow(stringdis, 110, 50, -1);
                        GL11.glPopMatrix();
                        break;
                    }

                    case "Exhibition":{
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(width / 2.0F + 10.0F - 2.0F, height - 200F, 0.0F);
                        RenderUtils.targetHudRect(0.0D, -2.0D, (tahoma.getStringWidth(target.getName()) > 70.0F) ? (124.0F + tahoma.getStringWidth(target.getName()) - 70.0F) : 124.0D, 38.0D, 1.0D);
                        RenderUtils.targetHudRect1(0.0D, -2.0D, 124.0D, 38.0D, 1.0D);
                        tahoma.drawString(target.getName(), 42.0F, 0.5F, -1);
                        float health = target.getHealth();
                        float totalHealth = target.getHealth() + target.getAbsorptionAmount();
                        float[] fractions = { 0.0F, 0.5F, 1.0F };
                        Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
                        float progress = health / target.getMaxHealth();
                        Color customColor = (health >= 0.0F) ? ColorManager.blendColors(fractions, colors, progress).brighter() : Color.RED;
                        double width1 = 0.0D;
                        width1 = ColorManager.getIncremental(width1, 5.0D);
                        if (width1 < 50.0D)
                            width1 = 50.0D;
                        double healthLocation = width1 * progress;
                        RenderUtils.rectangle(42.5D, 10.3D, 53.0D + healthLocation + 0.5D, 13.5D, customColor.getRGB());
                        if (target.getAbsorptionAmount() > 0.0F) RenderUtils.rectangle(97.5D - target.getAbsorptionAmount(), 10.3D, 103.5D, 13.5D, (new Color(137, 112, 9)).getRGB());
                        RenderUtils.drawRectBordered(42.0D, 9.800000190734863D, 54.0D + width1, 14.0D, 0.5D, ColorManager.getColor(0, 0), ColorManager.getColor(0));
                        for (int dist = 1; dist < 10; dist++) {
                            double dThing = width1 / 8.5D * dist;
                            RenderUtils.rectangle(43.5D + dThing, 9.8D, 43.5D + dThing + 0.5D, 14.0D, ColorManager.getColor(0));
                        }
                        GlStateManager.scale(0.5D, 0.5D, 0.5D);
                        int var18 = (int)mc.thePlayer.getDistanceToEntity(target);
                        String str = "HP: " + (int)totalHealth + " | Dist: " + var18;
                        mc.fontRendererObj.drawString(str, (int) 85.6F, (int) 32.0F, -1);
                        GlStateManager.scale(2.0D, 2.0D, 2.0D);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        if (target instanceof EntityPlayer)
                            drawArmor(target,28, 19);
                        GlStateManager.scale(0.31D, 0.31D, 0.31D);
                        GlStateManager.translate(73.0F, 102.0F, 40.0F);
                        model(target.rotationYaw, target.rotationPitch, target);
                        GlStateManager.popMatrix();
                        break;
                    }

                    case "Exhibition2":{
                        width = mc.displayWidth / (float) (mc.gameSettings.guiScale * 2) + 680;
                        height = mc.displayHeight / (float) (mc.gameSettings.guiScale * 2) + 280;
                        GlStateManager.pushMatrix();
                        // Width and height
                        GlStateManager.translate(width - 660, height - 160.0f - 90.0f, 0.0f);
                        // Draws the skeet rectangles.
                        RenderUtils.rectangle(4, -2, mc.fontRendererObj.getStringWidth(((EntityPlayer) target).getName()) > 70.0f ? (124.0D + mc.fontRendererObj.getStringWidth(((EntityPlayer) target).getName()) - 70.0f) : 124.0, 37.0, new Color(0, 0, 0, 160).getRGB());
                        // Draws name.
                        mc.fontRendererObj.drawStringWithShadow(((EntityPlayer) target).getName(), 42.3f, 0.3f, -1);
                        // Gets health.
                        final float health = ((EntityPlayer) target).getHealth();
                        // Gets health and absorption
                        final float healthWithAbsorption = ((EntityPlayer) target).getHealth() + ((EntityPlayer) target).getAbsorptionAmount();
                        // Color stuff for the healthBar.
                        final float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
                        final Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                        // Max health.
                        final float progress = health / ((EntityPlayer) target).getMaxHealth();
                        // Color.
                        final Color healthColor = health >= 0.0f ? ColorManager.blendColors(fractions, colors, progress).brighter() : Color.RED;
                        // Round.
                        double cockWidth = 0.0;
                        cockWidth = MathUtils.round(cockWidth, (int) 5.0);
                        if (cockWidth < 50.0) {
                            cockWidth = 50.0;
                        }
                        // Healthbar + absorption
                        final double healthBarPos = cockWidth * (double) progress;
                        RenderUtils.rectangle(42.5, 10.3, 53.0 + healthBarPos + 0.5, 13.5, healthColor.getRGB());
                        if (((EntityPlayer) target).getAbsorptionAmount() > 0.0f) {
                            RenderUtils.rectangle(97.5 - (double) ((EntityPlayer) target).getAbsorptionAmount(), 10.3, 103.5, 13.5, new Color(137, 112, 9).getRGB());
                        }
                        // Draws rect around health bar.
                        RenderUtils.rectangleBordered(42.0, 9.8f, 54.0 + cockWidth, 14.0, 0.5, 0, Color.BLACK.getRGB());
                        // Draws the lines between the healthbar to make it look like boxes.
                        for (int dist = 1; dist < 10; ++dist) {
                            final double cock = cockWidth / 8.5 * (double) dist;
                            RenderUtils.rectangle(43.5 + cock, 9.8, 43.5 + cock + 0.5, 14.0, Color.BLACK.getRGB());
                        }
                        // Draw targets hp number and distance number.
                        GlStateManager.scale(0.5, 0.5, 0.5);
                        final int distance = (int) mc.thePlayer.getDistanceToEntity(target);
                        final String nice = "HP: " + (int) healthWithAbsorption + " | Dist: " + distance;
                        mc.fontRendererObj.drawString(nice, 85.3f, 32.3f, -1, true);
                        GlStateManager.scale(2.0, 2.0, 2.0);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        // Draw targets armor and tools and weapons and shows the enchants.
                        if (target != null) drawEquippedShit(28, 20, target);
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                        // Draws targets model.
                        GlStateManager.scale(0.31, 0.31, 0.31);
                        GlStateManager.translate(73.0f, 102.0f, 40.0f);
                        drawModel(target.rotationYaw, target.rotationPitch, (EntityLivingBase) target);
                        GlStateManager.popMatrix();
                        break;
                    }
                }
            }
        }
    }

    private void model(float yaw, float pitch, EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 50.0F);
        GlStateManager.scale(-50.0F, 50.0F, 50.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float renderYawOffset = entityLivingBase.renderYawOffset;
        float rotationYaw = entityLivingBase.rotationYaw;
        float rotationPitch = entityLivingBase.rotationPitch;
        float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(-Math.atan((pitch / 40.0F)) * 20.0D), 1.0F, 0.0F, 0.0F);
        entityLivingBase.renderYawOffset = yaw - yaw / yaw * 0.4F;
        entityLivingBase.rotationYaw = yaw - yaw / yaw * 0.2F;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0F);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw((Entity)entityLivingBase, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    private void drawArmor(EntityLivingBase entityLivingBase, int x, int y) {
        GL11.glPushMatrix();
        List<ItemStack> stuff = new ArrayList<>();
        int split = -3;
        for (int index = 3; index >= 0; index--) {
            ItemStack armer = entityLivingBase.getCurrentArmor(index);
            if (armer != null)
                stuff.add(armer);
        }
        if (entityLivingBase.getHeldItem() != null)
            stuff.add(entityLivingBase.getHeldItem());
        for (ItemStack everything : stuff) {
            if (mc.theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                split += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            (mc.getRenderItem()).zLevel = -150.0F;
            mc.getRenderItem().renderItemIntoGUI(everything, split + x, y);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, everything, split + x, y);
//                RenderUtils.renderEnchantText(everything, split + x, y);
            (mc.getRenderItem()).zLevel = 0.0F;
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            everything.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }

    private void drawArmor2(EntityLivingBase entityLivingBase, int x, int y){
        GL11.glPushMatrix();
        List<ItemStack> stuff = new ArrayList<>();
        int split = -3;
        for (int index = 3; index >= 0; index--) {
            ItemStack armer = entityLivingBase.getCurrentArmor(index);
            if (armer != null)
                stuff.add(armer);
        }
        if (entityLivingBase.getHeldItem() != null)
            stuff.add(entityLivingBase.getHeldItem());
        for (ItemStack everything : stuff) {
            if (mc.theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                split += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            (mc.getRenderItem()).zLevel = -150.0F;
            mc.getRenderItem().renderItemIntoGUI(everything, split + x, y);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, everything, split + x, y);
//                RenderUtils.renderEnchantText(everything, split + x, y);
            (mc.getRenderItem()).zLevel = 0.0F;
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            everything.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }

    public static void drawModel(final float yaw, final float pitch, final EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        final float renderYawOffset = entityLivingBase.renderYawOffset;
        final float rotationYaw = entityLivingBase.rotationYaw;
        final float rotationPitch = entityLivingBase.rotationPitch;
        final float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        final float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) (-Math.atan(pitch / 40.0f) * 20.0), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - yaw / yaw * 0.4f;
        entityLivingBase.rotationYaw = yaw - yaw / yaw * 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    private void drawEquippedShit(final int x, final int y, Entity target) {
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        for (int geraltOfNigeria = 3; geraltOfNigeria >= 0; --geraltOfNigeria) {
            final ItemStack armor = ((EntityPlayer) target).getCurrentArmor(geraltOfNigeria);
            if (armor != null) {
                stuff.add(armor);
            }
        }
        if (((EntityPlayer) target).getHeldItem() != null) {
            stuff.add(((EntityPlayer) target).getHeldItem());
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, yes, cock + x, y);
//            RenderUtils.renderEnchantText(yes, cock + x, (y + 0.5f));
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            yes.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }
}
