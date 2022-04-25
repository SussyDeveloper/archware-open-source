package archware.module.impl.render;

import archware.utils.font.FontManager;
import archware.utils.render.RenderUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import archware.Client;
import archware.event.Event;
import archware.event.impl.Event2D;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.Wrapper;
import archware.utils.render.Translate;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HUD extends Module {

    ModeSetting watermarkMode = new ModeSetting("WaterMark Theme", "Normal", "Normal", "OneTap", "Skeet");
    ModeSetting arraylist = new ModeSetting("ArrayList Color", "Color", "Color", "Pulse", "Category");
    public static NumberSetting red = new NumberSetting("Red", 255, 0, 255, 1, true);
    public static NumberSetting green = new NumberSetting("Green", 255, 0, 255, 1, true);
    public static NumberSetting blue = new NumberSetting("Blue", 255, 0, 255, 1, true);
    NumberSetting opacity = new NumberSetting("Opacity", 0, 0, 255, 1, true);
    BoolSetting showArray = new BoolSetting("ArrayList", true);
    BoolSetting showWater = new BoolSetting("WaterMark", true);
    BoolSetting showUser = new BoolSetting("User-Info", true);
    BoolSetting cfontproperty = new BoolSetting("CFont", true);
    BoolSetting backgroundarrayList = new BoolSetting("Background", false);
    BoolSetting showPotions = new BoolSetting("Potions", true);
    BoolSetting showArmor = new BoolSetting("Armor HUD", true);
    ModeSetting animationMode = new ModeSetting("Animation", "Animation 1", "Animation 1", "Animation 2");
    public static int colorInstance;


    FontRenderer fr = mc.fontRendererObj;
    FontManager cfont = new FontManager("cfont", Font.PLAIN, 18, 1, 1);
    FontManager cfont2 = new FontManager("cfont2", Font.PLAIN, 18, 1, 1);

    public HUD() {
        super("HUD", "It shows client's hud", 0, Category.RENDER);
        setEnabled(true);
        setHidden(true);
        addSettings(watermarkMode,arraylist, animationMode, red, green, blue, opacity, showWater, showArray, showUser, showArmor, cfontproperty, backgroundarrayList);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof Event2D){
            drawArmor(showArmor.isEnable());
            drawArrayList(showArray.isEnable());
            drawWatermark(showWater.isEnable());
//            drawUserInfo(showUser.isEnable());
            drawPotionInfo(showPotions.isEnable());
        }
    }

    void drawArrayList(boolean show){
        if(show) {
            ScaledResolution sr = new ScaledResolution(mc);
            ArrayList<Module> modules = new ArrayList<>(Client.moduleManager.getModules());
            if (cfontproperty.isEnable())
                modules.sort((m1, m2) -> (int) (cfont2.getWidth(m2.getDisplayName()) - cfont2.getWidth(m1.getDisplayName())));
            else
                modules.sort((m1, m2) -> (fr.getStringWidth(m2.getDisplayName()) - fr.getStringWidth(m1.getDisplayName())));
            int y = cfontproperty.isEnable() ? 0 : 2;
            int heightOffset = cfontproperty.isEnable() ? 11 : 10;
            int screenX = sr.getScaledWidth();
            int i = 0;
            for (Module m : modules) {
                Translate translate = m.getTranslate();
                if (m.isEnabled() && !m.isHidden()) {
                    float moduleWidth = cfontproperty.isEnable() ? cfont2.getWidth(m.getDisplayName()) : fr.getStringWidth(m.getDisplayName());
                    if(animationMode.is("Animation 1")){
                        translate.animate(screenX - moduleWidth - 2, y, 0.5);
                    }else{
                        translate.translate(screenX - moduleWidth - 2, y);
                    }
                    y += 11;
                } else {
                    if(animationMode.is("Animation 1")) {
                        translate.animate(screenX, y, 0.5);
                    }else{
                        translate.animate(screenX, -25);
                    }
                }
                boolean shown = translate.getX() < screenX;
                if (shown) {
                    int arrayColor = new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue()).getRGB();
                    int color = new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue()).getRGB();
                    switch (arraylist.getSelected()){
                        case "Color":{
                            arrayColor = new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue()).getRGB();
                            break;
                        }
                        case "Pulse":{
                            arrayColor = fadeBetween(color, darker(color, 0.5f), ((System.currentTimeMillis() + (y * 100)) % 1200 / (1200 / 2.0F)));
                            break;
                        }
                        case "Category":{
                            if(m.getCategory() == Category.COMBAT){
                                arrayColor = new Color(252, 112, 112).getRGB();
                            }
                            if(m.getCategory() == Category.MOVEMENT){
                                arrayColor = new Color(144, 224, 255).getRGB();
                            }
                            if(m.getCategory() == Category.PLAYER){
                                arrayColor = new Color(69, 186, 255).getRGB();
                            }
                            if(m.getCategory() == Category.RENDER){
                                arrayColor = new Color(252, 212, 112).getRGB();
                            }
                            if(m.getCategory() == Category.OTHER){
                                arrayColor = new Color(226, 112, 252).getRGB();
                            }
                            break;
                        }
                    }

                    if (backgroundarrayList.isEnable())
                        Gui.drawRect(translate.getX() - 3, translate.getY() - 1, screenX, translate.getY() + heightOffset, new Color(0,0,0, (int) opacity.getValue()).getRGB());
//                    Gui.drawRect(translate.getX() - 1, translate.getY(), screenX, translate.getY() + heightOffset, 0x500D0D0D);
                    if (cfontproperty.isEnable())
                        cfont2.drawStringWithShadow(m.getDisplayName(), (float) translate.getX(), (float) translate.getY(), arrayColor);
                    else
                        fr.drawStringWithShadow(m.getDisplayName(), (float) translate.getX(), (float) translate.getY(), arrayColor);
                }
                i++;
            }
        }
    }

    void drawWatermark(boolean show){
        if(show) {
            int color = new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue()).getRGB();
            switch (arraylist.getSelected()){
                case "Color":{
                    colorInstance = new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue()).getRGB();
                    break;
                }
                case "Pulse":{
                    colorInstance = fadeBetween(color, darker(color, 0.5f), ((System.currentTimeMillis() + (11 * 100)) % 1200 / (1200 / 2.0F)));
                    break;
                }
            }
            switch (watermarkMode.getSelected()){
                case "Normal":{
                    if (cfontproperty.isEnable())
                        cfont2.drawStringWithShadow(Client.name, 2, 0, colorInstance);
                    else
                        fr.drawStringWithShadow(Client.name, 2, 2, colorInstance);
                    break;
                }
                case "OneTap":{
                    Gui.drawRect(2, 4, fr.getStringWidth("Archware FPS: " + Minecraft.getDebugFPS()) + 7, fr.FONT_HEIGHT + 6, new Color(0,0,0, 150).getRGB());
                    Gui.drawRect(2, 2, fr.getStringWidth("Archware FPS: " + Minecraft.getDebugFPS()) + 7, 4, colorInstance);
                    RenderUtils.drawOutlinedStringBold(Client.name + " FPS: " + Minecraft.getDebugFPS(), 5, 5, -1, new Color(0,0,0).getRGB());
                    break;
                }
            }
        }
    }

/*    void drawUserInfo(boolean show){
        if(show){
            ScaledResolution sr = new ScaledResolution(mc);
            String information = ChatFormatting.GRAY + Client.getBuild() + " - " + ChatFormatting.WHITE + ChatFormatting.BOLD + Client.getVersion() + ChatFormatting.GRAY + ChatFormatting.BOLD + " - " + "muraz";
            if (cfontproperty.isEnable())
                cfont2.drawStringWithShadow(information, sr.getScaledWidth() - cfont2.getWidth(information) - 2, sr.getScaledHeight() - cfont2.getHeight(information), -1);
            else
                fr.drawStringWithShadow(information, sr.getScaledWidth() - fr.getStringWidth(information) + 5, sr.getScaledHeight() - fr.FONT_HEIGHT, -1);
        }
    }*/

    void drawPotionInfo(boolean show){
        ScaledResolution sr = new ScaledResolution(mc);
        int screenX = sr.getScaledWidth();
        int screenY = sr.getScaledHeight();
        if(show){
            String information = ChatFormatting.GRAY + Client.getBuild() + " - " + ChatFormatting.WHITE + Client.getVersion() + ChatFormatting.GRAY + " - " + "muraz";
            float informationHeight = cfontproperty.isEnable() ? cfont2.getHeight(information) : fr.FONT_HEIGHT;
            int potionY = (int) informationHeight + (cfontproperty.isEnable() ? 10 : 11);
            for (PotionEffect effect : Wrapper.getPlayer().getActivePotionEffects()) {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                String effectName = I18n.format(
                        potion.getName()) + " " +
                        (effect.getAmplifier() + 1) +
                        " \2477" +
                        Potion.getDurationString(effect);
                if(cfontproperty.isEnable()){
                    cfont2.drawStringWithShadow(effectName,
                            screenX - 2 - cfont2.getWidth(effectName),
                            screenY - potionY,
                            potion.getLiquidColor());
                }else {
                    fr.drawStringWithShadow(effectName,
                            screenX - 2 - fr.getStringWidth(effectName),
                            screenY - potionY,
                            potion.getLiquidColor());
                }

                potionY += cfontproperty.isEnable() ? cfont2.getHeight(effectName) : fr.FONT_HEIGHT;
            }
        }
    }

    void drawArmor(boolean show){
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glPushMatrix();
        if(show){
            List<ItemStack> stuff = new ArrayList<>();
            boolean onWater = (mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water));
            int split = -3;
            for (int index = 3; index >= 0; index--) {
                ItemStack armer = mc.thePlayer.inventory.armorInventory[index];
                if (armer != null)
                    stuff.add(armer);
            }
            if (mc.thePlayer.getCurrentEquippedItem() != null)
                stuff.add(mc.thePlayer.getCurrentEquippedItem());
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
                mc.getRenderItem().renderItemIntoGUI(everything, split + scaledResolution.getScaledWidth() / 2 - 4,
                        scaledResolution.getScaledHeight() - (onWater ? 65 : 55) + (mc.thePlayer.capabilities.isCreativeMode ? 14 : 0));
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, everything, split + scaledResolution.getScaledWidth() / 2 - 4,
                        scaledResolution.getScaledHeight() - (onWater ? 65 : 55) + (mc.thePlayer.capabilities.isCreativeMode ? 14 : 0));
//                RenderUtils.renderEnchantText(everything, split + scaledResolution.getScaledWidth() / 2 - 3,
//                        scaledResolution.getScaledHeight() * 2 - (onWater ? 145 : 135) + (mc.thePlayer.capabilities.isCreativeMode ? 14 : 0));
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
        }
        GL11.glPopMatrix();
    }

    private int darker(int color, float factor) {
        int r = (int) ((color >> 16 & 0xFF) * factor);
        int g = (int) ((color >> 8 & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;

        return ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF) |
                ((a & 0xFF) << 24);
    }

    private int fadeBetween(int color1, int color2, float offset) {
        if (offset > 1)
            offset = 1 - offset % 1;

        double invert = 1 - offset;
        int r = (int) ((color1 >> 16 & 0xFF) * invert +
                (color2 >> 16 & 0xFF) * offset);
        int g = (int) ((color1 >> 8 & 0xFF) * invert +
                (color2 >> 8 & 0xFF) * offset);
        int b = (int) ((color1 & 0xFF) * invert +
                (color2 & 0xFF) * offset);
        int a = (int) ((color1 >> 24 & 0xFF) * invert +
                (color2 >> 24 & 0xFF) * offset);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF);
    }


}
