package archware.utils.render;

import archware.Client;
import archware.module.impl.combat.KillAura;
import archware.module.impl.combat.TargetStrafe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;
import archware.utils.MathUtils;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static archware.module.Module.mc;

public class RenderUtils {

    private static final Frustum FRUSTUM = new Frustum();
    private static final double DOUBLE_PI = Math.PI * 2;

    public static void drawOutlinedString(String s, float x, float y, int color, int outlineColor) {
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x - 0.5F, y, outlineColor);
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x, y - 0.5F, outlineColor);
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x + 0.5F, y, outlineColor);
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x, y + 0.5F, outlineColor);
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x, y, color);
    }

    public static void drawOutlinedStringBold(String s, float x, float y, int color, int outlineColor) {
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x - 1F, y, outlineColor);
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x, y - 1F, outlineColor);
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x + 1F, y, outlineColor);
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x, y + 1F, outlineColor);
        (Minecraft.getMinecraft()).fontRendererObj.drawString(s, x, y, color);
    }

    public void push() {
        GL11.glPushMatrix();
    }

    public void pop() {
        GL11.glPopMatrix();
    }

    public static void enable(final int glTarget) {
        GL11.glEnable(glTarget);
    }

    public static void disable(final int glTarget) {
        GL11.glDisable(glTarget);
    }

    public static void start() {
        enable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        disable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_CULL_FACE);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        enable(GL11.GL_CULL_FACE);
        enable(GL11.GL_TEXTURE_2D);
        disable(GL11.GL_BLEND);
        color(Color.white);
    }


    public static void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }

    public static void drawLinesAroundPlayer(Entity entity,
                                             double radius,
                                             float partialTicks,
                                             int points,
                                             float width,
                                             int color) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glDisable(GL_DEPTH_TEST);
        glLineWidth(width);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glBegin(GL_LINE_STRIP);
        final double x = RenderUtils.interpolate(entity.prevPosX, entity.posX, partialTicks) - RenderManager.viewerPosX;
        final double y = RenderUtils.interpolate(entity.prevPosY, entity.posY, partialTicks) - RenderManager.viewerPosY;
        final double z = RenderUtils.interpolate(entity.prevPosZ, entity.posZ, partialTicks) - RenderManager.viewerPosZ;
        RenderUtils.color(Client.moduleManager.getModuleByName("Speed").isEnabled() && TargetStrafe.getSpeedProperty().isEnable()
                || Client.moduleManager.getModuleByName("Flight").isEnabled() && TargetStrafe.getFlightProperty().isEnable()
                ? color : new Color(255, 255, 255).getRGB());
        for (int i = 0; i <= points; i++)
            glVertex3d(
                    x + radius * Math.cos(i * DOUBLE_PI / points),
                    y,
                    z + radius * Math.sin(i * DOUBLE_PI / points));
        glEnd();
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x1 = x + width;
        double y1 = y + height;
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);

        x *= 2;
        y *= 2;
        x1 *= 2;
        y1 *= 2;

        glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(f1, f2, f3, f);
        glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_POLYGON);

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + +(Math.sin((i * Math.PI / 180)) * (radius * -1)), y + radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + (Math.sin((i * Math.PI / 180)) * (radius * -1)), y1 - radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
        }

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius), y1 - radius + (Math.cos((i * Math.PI / 180)) * radius));
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius), y + radius + (Math.cos((i * Math.PI / 180)) * radius));
        }

        GL11.glEnd();

        glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);

        GL11.glScaled(2, 2, 2);

        GL11.glPopAttrib();
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

    }

    public static int getColorFromPercentage(float percentage) {
        return Color.HSBtoRGB(percentage / 3, 1.0F, 1.0F);
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(Color color) {
        float red = color.getRed() / 255.0F;
        float green = color.getGreen() / 255.0F;
        float blue = color.getBlue() / 255.0F;
        float alpha = color.getAlpha() / 255.0F;
        GlStateManager.color(red, green, blue, alpha);
    }

    public static void color(int color) {
        glColor4ub(
                (byte) (color >> 16 & 0xFF),
                (byte) (color >> 8 & 0xFF),
                (byte) (color & 0xFF),
                (byte) (color >> 24 & 0xFF));
    }

    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }


    public static double interpolate(double old,
                                     double now,
                                     float partialTicks) {
        return old + (now - old) * partialTicks;
    }

    public static float interpolate(float old,
                                    float now,
                                    float partialTicks) {
        return old + (now - old) * partialTicks;
    }

    public static boolean isBBInFrustum(AxisAlignedBB aabb) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        FRUSTUM.setPosition(player.posX, player.posY, player.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(aabb);
    }

    public static double progressiveAnimation(double now, double desired, double speed) {
        double dif = Math.abs(now - desired);
        int fps = Minecraft.getDebugFPS();
        if (dif > 0.0D) {
            double animationSpeed = MathUtils.roundToDecimalPlace(Math.min(10.0D,
                    Math.max(0.05D, 144.0D / fps * dif / 10.0D * speed)), 0.05D);
            if (dif != 0.0D && dif < animationSpeed)
                animationSpeed = dif;
            if (now < desired)
                return now + animationSpeed;
            if (now > desired)
                return now - animationSpeed;
        }
        return now;
    }

    public static double linearAnimation(double now, double desired, double speed) {
        double dif = Math.abs(now - desired);
        int fps = Minecraft.getDebugFPS();
        if (dif > 0.0D) {
            double animationSpeed = MathUtils.roundToDecimalPlace(Math.min(10.0D,
                    Math.max(0.005D, 144.0D / fps * speed)), 0.005D);
            if (dif != 0.0D && dif < animationSpeed)
                animationSpeed = dif;
            if (now < desired)
                return now + animationSpeed;
            if (now > desired)
                return now - animationSpeed;
        }
        return now;
    }

    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x * factor), (int)((scale.getScaledHeight() - y2) * factor), (int)((x2 - x) * factor), (int)((y2 - y) * factor));
    }

    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public static void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

    public static void lineNoGl(final double firstX, final double firstY, final double secondX, final double secondY, final Color color) {
        start();
        if (color != null)
            color(color);
        lineWidth(1);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);
        {
            vertex(firstX, firstY);
            vertex(secondX, secondY);
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }
    public static void lineNoGl(final double firstX, final double firstY, final double secondX, final double secondY, final Color color, float linewidth) {

        start();
        if (color != null)
            color(color);
        lineWidth(linewidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);
        {
            vertex(firstX, firstY);
            vertex(secondX, secondY);
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public void line(final double firstX, final double firstY, final double secondX, final double secondY, final double lineWidth, final Color color) {
        start();
        if (color != null)
            color(color);
        lineWidth(lineWidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);
        {
            vertex(firstX, firstY);
            vertex(secondX, secondY);
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public static void lineWidth(final double width) {
        GL11.glLineWidth((float) width);
    }

    public void line(final double firstX, final double firstY, final double secondX, final double secondY, final double lineWidth) {
        line(firstX, firstY, secondX, secondY, lineWidth, null);
    }

    public void line(final double firstX, final double firstY, final double secondX, final double secondY, final Color color) {
        line(firstX, firstY, secondX, secondY, 0, color);
    }

    public void line(final double firstX, final double firstY, final double secondX, final double secondY) {
        line(firstX, firstY, secondX, secondY, 0, null);
    }

    public static boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        FRUSTUM.setPosition(current.posX, current.posY, current.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(bb);
    }

    public static void drawRectBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        rectangle(x + width, y, x1 - width, y + width, borderColor);
        rectangle(x, y, x + width, y1, borderColor);
        rectangle(x1 - width, y, x1, y1, borderColor);
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
    }

    public static void rectangle(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            double var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, top, 0.0D).endVertex();
        worldRenderer.pos(left, top, 0.0D).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void targetHudRect(double x, double y, double x1, double y1, double size) {
        rectangleBordered(x, y + -4.0D, x1 + size, y1 + size, 0.5D, (new Color(60, 60, 60)).getRGB(), (new Color(10, 10, 10)).getRGB());
        rectangleBordered(x + 1.0D, y + -3.0D, x1 + size - 1.0D, y1 + size - 1.0D, 1.0D, (new Color(40, 40, 40)).getRGB(), (new Color(40, 40, 40)).getRGB());
        rectangleBordered(x + 2.5D, y + -1.5D, x1 + size - 2.5D, y1 + size - 2.5D, 0.5D, (new Color(40, 40, 40)).getRGB(), (new Color(60, 60, 60)).getRGB());
        rectangleBordered(x + 2.5D, y + -1.5D, x1 + size - 2.5D, y1 + size - 2.5D, 0.5D, (new Color(22, 22, 22)).getRGB(), (new Color(255, 255, 255, 0)).getRGB());
    }

    public static void targetHudRect1(double x, double y, double x1, double y1, double size) {
        rectangleBordered(x + 4.35D, y + 0.5D, x1 + size - 84.5D, y1 + size - 4.35D, 0.5D, (new Color(48, 48, 48)).getRGB(), (new Color(10, 10, 10)).getRGB());
        rectangleBordered(x + 5.0D, y + 1.0D, x1 + size - 85.0D, y1 + size - 5.0D, 0.5D, (new Color(17, 17, 17)).getRGB(), (new Color(255, 255, 255, 0)).getRGB());
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawImage(ResourceLocation image, float x, float y, int width, int height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        float f = 1.0F / width;
        float f1 = 1.0F / height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, (y + height), 0.0D).tex((0.0F * f), (height * f1)).endVertex();
        worldrenderer.pos((x + width), (y + height), 0.0D).tex((width * f), (height * f1)).endVertex();
        worldrenderer.pos((x + width), y, 0.0D).tex((width * f), (0.0F * f1)).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex((0.0F * f), (0.0F * f1)).endVertex();
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawAuraMark(Entity entity, Color color) {
        KillAura killaura = new KillAura();
        Timer timer = (Minecraft.getMinecraft()).timer;
        if (killaura == null)
            return;
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - RenderManager.renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - RenderManager.renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - RenderManager.renderPosZ;
        AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().offset(-entity.posX, -entity.posY, -entity.posZ).offset(x, y - 0.41D, z);
        drawAxisAlignedBB(new AxisAlignedBB(axisAlignedBB.minX, axisAlignedBB.maxY + 0.2D, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY + 0.26D, axisAlignedBB.maxZ), color);
    }

    public static void drawPlatform(Entity entity, Color color) {
        Timer timer = (Minecraft.getMinecraft()).timer;
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - RenderManager.renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - RenderManager.renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - RenderManager.renderPosZ;
        AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().offset(-entity.posX, -entity.posY, -entity.posZ).offset(x, y, z);
        drawAxisAlignedBB(new AxisAlignedBB(axisAlignedBB.minX - 0.1D, axisAlignedBB.minY - 0.1D, axisAlignedBB.minZ - 0.1D, axisAlignedBB.maxX + 0.1D, axisAlignedBB.maxY + 0.2D, axisAlignedBB.maxZ + 0.1D), color);
    }
    public static void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, Color color) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        glColor(color);
        drawFilledBox(axisAlignedBB);
        GlStateManager.resetColor();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }

    public static void drawFilledBox(AxisAlignedBB axisAlignedBB) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    // Sometimes colors get messed up in for loops, so we use this method to reset it to allow new colors to be used
    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }


    public static void disableSmoothLine() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void enableSmoothLine(float width) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }
}
