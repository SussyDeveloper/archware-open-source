package archware.module.impl.render;


import archware.event.impl.Event3D;
import archware.event.impl.EventNameTag;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.render.EspUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import archware.event.Event;
import archware.event.impl.Event2D;
import archware.module.Category;
import archware.module.Module;
import archware.module.impl.other.HackerDetector;
import archware.utils.ColorManager;
import archware.utils.render.RenderUtils;


import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import org.lwjgl.util.vector.Vector4f;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class ESP extends Module {
    ModeSetting colorMode = new ModeSetting("Color", "Custom", "Custom", "HUD");
    NumberSetting red = new NumberSetting("Red", 255, 0, 255, 1, true);
    NumberSetting green = new NumberSetting("Green", 255, 0, 255, 1, true);
    NumberSetting blue = new NumberSetting("Blue", 255, 0, 255, 1, true);
    BoolSetting boxEsp = new BoolSetting("Box", true);
    BoolSetting nameTags = new BoolSetting("Tags", true);
    BoolSetting healthBar = new BoolSetting("Health Bar", true);
    BoolSetting players = new BoolSetting("Players", true);
    BoolSetting animals = new BoolSetting("Animals", false);
    BoolSetting mobs = new BoolSetting("Mobs", false);
    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();
    boolean isHacker;

    public ESP() {
        super("ESP", "It shows 2d boxes in the wall", 0, Category.RENDER);
        addSettings(colorMode, red, green, blue, boxEsp, nameTags, healthBar, players, animals, mobs);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventNameTag){
            if(nameTags.isEnable()){
                event.setCancelled(true);
            }
        }

        if(event instanceof Event3D){
            entityPosition.clear();
            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (shouldRender(entity) && EspUtils.isInView(entity)) {
                    entityPosition.put(entity, EspUtils.getEntityPositionsOn2D(entity));
                }
            }
        }

        if(event instanceof Event2D){
            for(Entity entity : entityPosition.keySet()){
                Vector4f pos = entityPosition.get(entity);
                float x = pos.getX(),
                        y = pos.getY(),
                        right = pos.getZ(),
                        bottom = pos.getW();
                if (nameTags.isEnable() && entity instanceof EntityLivingBase) {
                    isHacker = HackerDetector.isHacker((EntityLivingBase) entity);
                    EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                    StringBuilder text = new StringBuilder(StringUtils.stripControlCodes(renderingEntity.getName()));
                    double fontScale = .5;
                    float middle = x + ((right - x) / 2);
                    float textWidth = 0;
                    double fontHeight;

                    textWidth = mc.fontRendererObj.getStringWidth(text.toString());
                    middle -= (textWidth * fontScale) / 2f;
                    fontHeight = mc.fontRendererObj.FONT_HEIGHT * fontScale;

                    glPushMatrix();
                    glTranslated(middle, y - (fontHeight), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(y - (fontHeight)), 0);

                    int color;
                    if(isHacker){
                        color = Color.RED.getRGB();
                    }else{
                        color = -1;
                    }

                    mc.fontRendererObj.drawStringWithShadow(text.toString(), middle, (float) (y - (fontHeight + 4)), color);
                    glPopMatrix();
                }

                if(healthBar.isEnable() && entity instanceof EntityLivingBase){
                    EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                    float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                    Color healthColor = healthValue > .75 ?
                            Color.GREEN : healthValue > .5
                            ? Color.YELLOW : healthValue > .35
                            ? new Color(236, 100, 64) : new Color(255, 65, 68);

                    float height = (bottom - y) + 1;
                    Gui.drawRect2(x - 3.5F, y - .5f, 2, height + 1, new Color(0, 0, 0, 180).getRGB());
                    Gui.drawRect2(x - 3F, y + (height - (height * healthValue)), 1, height * healthValue, healthColor.getRGB());
                }

                if(boxEsp.isEnable()){
                    Color color = new Color(255, 255, 255);
                    if(colorMode.is("Custom")){
                        color = new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue());
                    }
                    if(colorMode.is("HUD")){
                        color = new Color((int) HUD.red.getValue(), (int) HUD.green.getValue(), (int) HUD.blue.getValue());
                    }
                    RenderUtils.resetColor();

                    //top
                    Gui.drawRect2(x, y, (right - x), 0.5F, color.getRGB());
                    //left
                    Gui.drawRect2(x, y, 0.5F, bottom - y, color.getRGB());
                    //bottom
                    Gui.drawRect2(x, bottom, right - x, 0.5F, color.getRGB());
                    //right
                    Gui.drawRect2(right, y, 0.5F, (bottom - y) + 0.5F, color.getRGB());

                    //Outline
                    float outlineThickness = .5f;
                    Gui.drawRect2(x - .5f, y - outlineThickness, (right - x) + 2, outlineThickness, Color.BLACK.getRGB());
                    //Left
                    Gui.drawRect2(x - outlineThickness, y, outlineThickness, (bottom - y) + 1, Color.BLACK.getRGB());
                    //bottom
                    Gui.drawRect2(x - .5f, (bottom + 1), (right - x) + 2, outlineThickness, Color.BLACK.getRGB());
                    //Right
                    Gui.drawRect2(right + 1, y, outlineThickness, (bottom - y) + 1, Color.BLACK.getRGB());


                    //top
                    Gui.drawRect2(x + 1, y + 1, (right - x) - 1, outlineThickness, Color.BLACK.getRGB());
                    //Left
                    Gui.drawRect2(x + 1, y + 1, outlineThickness, (bottom - y) - 1, Color.BLACK.getRGB());
                    //bottom
                    Gui.drawRect2(x + 1, (bottom - outlineThickness), (right - x) - 1, outlineThickness, Color.BLACK.getRGB());
                    //Right
                    Gui.drawRect2(right - outlineThickness, y + 1, outlineThickness, (bottom - y) - 1, Color.BLACK.getRGB());
                }
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


    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (players.isEnable() && entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return true;
        }
        if (animals.isEnable() && entity instanceof EntityAnimal) {
            return true;
        }

        return mobs.isEnable() && entity instanceof EntityMob;
    }


}
