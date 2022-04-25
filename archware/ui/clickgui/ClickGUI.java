package archware.ui.clickgui;

import archware.utils.render.Translate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.ResourceLocation;
import archware.Client;
import archware.module.Category;
import archware.module.Module;
import archware.module.impl.render.ClickGui;
import archware.module.settings.Setting;
import archware.module.settings.Slider;
import archware.module.settings.impl.BoolSetting;
import archware.module.settings.impl.ModeSetting;
import archware.module.settings.impl.NumberSetting;
import archware.utils.font.FontManager;
import archware.utils.render.RenderUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;


import java.awt.*;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {

    static FontManager font = new FontManager("font", Font.BOLD, 16, 1,1);
    static FontManager sfont = new FontManager("font", Font.BOLD, 12, 1,1);

    public double posX;
    public double posY;
    double dragX;
    double dragY;
    double width;
    double height;
    boolean dragging, showSettings;
    Category selectedCategory = Category.COMBAT;
    Module selectedModule;
    Translate translate = new Translate(0,0);

    public ClickGUI(){
        posX = sr().getScaledWidth() / 2 - 100;
        posY = sr().getScaledWidth() / 2 - 180;
        showSettings = false;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        int color = (new Color((int) ClickGui.red.getValue(), (int) ClickGui.green.getValue(), (int) ClickGui.blue.getValue())).getRGB();
        if (dragging) {
            posX = mouseX - dragX;
            posY = mouseY - dragY;
        }

        //Resolution
        width = posX + 300.0D;
        height = posY + 280.0;

        if(ClickGui.background.isEnable()) {
            drawGradientRect(0, sr().getScaledHeight() - 300, sr().getScaledWidth(), sr().getScaledHeight(), 0, color);
        }

        RenderUtils.drawRoundedRect(posX, posY - 5, 300.0D, 280.0, 10, new Color(14, 14, 14, 255).getRGB());
        Gui.drawRect(posX, posY - 7.0D, width, posY, color);

        Gui.drawRect(posX, posY, width, posY + 23, new Color(26, 26, 26).getRGB());
        int x = 10;
        for(Category c : Category.values()){
            Gui.drawRect(posX + x - 3, posY , posX + x + font.getStringWidth(c.name() + 3), posY + 23, c.equals(selectedCategory) ? color : 0);
            font.drawString(c.name(), (int)posX + x, (int)posY + 7, -1);
            x += font.getStringWidth(c.name()) + 15;
        }

        mdraw((int) posX, (int) posY + 30, selectedCategory);

        if(showSettings){
            sdraw(mouseX, mouseY,(int) posX, (int) posY + 30, selectedModule);
        }

        if(ClickGui.cursor.isEnable()){
            mc.getTextureManager().bindTexture(new ResourceLocation("archware/textures/cursor.png"));
            drawModalRectWithCustomSizedTexture(mouseX, mouseY, 0,0,14, 15, 14, 15);
        }
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInside(mouseX, mouseY, posX, posY - 7, width, posY) && mouseButton == 0) {
            this.dragging = true;
            this.dragX = mouseX - posX;
            this.dragY = mouseY - posY;
        }

        int x = 10;
        for(Category c : Category.values()){
            if(isInside(mouseX, mouseY, posX + x - 3, posY + 2, posX + x + font.getStringWidth(c.name() + 3), posY + 23) && mouseButton == 0){
                selectedCategory = c;
            }
            x += font.getStringWidth(c.name()) + 15;
        }

        mclicked(mouseX, mouseY, mouseButton, (int) posX, (int) posY + 30, selectedCategory);
        if(showSettings){
            sclicked(mouseX, mouseY, mouseButton, (int) posX, (int) posY + 30, selectedModule);
        }
    }

    void mdraw(int X, int Y, Category selected){
        int color = (new Color((int) ClickGui.red.getValue(), (int) ClickGui.green.getValue(), (int) ClickGui.blue.getValue())).getRGB();
        ArrayList<Module> modules = new ArrayList<>(Client.moduleManager.getModulesByCategory(selected));
        modules.sort((m1, m2) -> getFont().getStringWidth(m2.getName()) - getFont().getStringWidth(m1.getName()));
        int posY = Y;
        for(Module m : modules){
            Gui.drawRect(X + 10, posY + 5, X + 60, posY + 25, new Color(0, 0, 0, 205).getRGB());
            Gui.drawRect(X + 11, posY + 6, X + 59, posY + 24, new Color(26, 26, 26, 205).getRGB());
            getFont().drawString(m.getName(), X + 10, posY, -1);
            getFont().drawString("Enable", X + 15, posY + 10, -1);
            RenderUtils.drawRoundedRect(X + 45, posY + 12, 7, 7, 2, m.isEnabled() ? color : new Color(0, 0, 0, 205).getRGB());
            posY += 30;
        }
    }

    void mclicked(int mouseX, int mouseY, int mouseButton, int X, int Y, Category selected){
        ArrayList<Module> modules = new ArrayList<>(Client.moduleManager.getModulesByCategory(selected));
        modules.sort((m1, m2) -> getFont().getStringWidth(m2.getName()) - getFont().getStringWidth(m1.getName()));

        int posY = Y;
        for(Module m : modules){
            if(isInside(mouseX, mouseY, X + 43, posY + 12, X + 52, posY + 19) && mouseButton == 0){
                m.toggle();
            }

            if(isInside(mouseX, mouseY, X + 15, posY + 12, X + 40, posY + 19) && mouseButton == 0){
                showSettings = true;
                selectedModule = m;
            }
            posY += 30;
        }
    }

    void sclicked(int mouseX, int mouseY, int mouseButton, int X, int Y, Module selected){
        int posY2 = Y;
        int posY3 = Y;
        for(Setting s : selected.getSettings()){
            if(s instanceof ModeSetting){
                ModeSetting modeSetting = (ModeSetting) s;
                if(isInside(mouseX, mouseY, X + 130, posY2 + 9, X + font.getStringWidth(modeSetting.getSelected()) + 130,posY2 + 20) && mouseButton == 0){
                    modeSetting.cycle();
                }
                posY2 += 30;
            }

            if(s instanceof BoolSetting){
                BoolSetting boolSetting = (BoolSetting)s;
                if(isInside(mouseX, mouseY, X + 105, posY3 + 12, X + 112,posY3 + 19) && mouseButton == 0){
                    boolSetting.toggle();
                }
                posY3 += 30;
            }
        }
    }

    void sdraw(int mouseX, int mouseY, int X, int Y, Module selected){
        int color = (new Color((int) ClickGui.red.getValue(), (int) ClickGui.green.getValue(), (int) ClickGui.blue.getValue())).getRGB();
        int posY = Y;
        int posY2 = Y;
        int posY3 = Y;
        for(Setting s : selected.getSettings()){
            if(s instanceof NumberSetting){
                NumberSetting numberSetting = (NumberSetting) s;
                Gui.drawRect(X + 199, posY + 5, X + 290, posY + 25, new Color(0, 0, 0, 205).getRGB());
                Gui.drawRect(X + 200, posY + 6, X + 289, posY + 24, new Color(26, 26, 26, 205).getRGB());
                font.drawString(s.name,X + 198, posY, -1);
                DecimalFormat format = new DecimalFormat("#0.0");
                format.applyPattern("#0.0");
                String number = format.format(numberSetting.value);
                sfont.drawString(number, X + 270, posY + 4, -1);
                s.slider = new Slider((int) numberSetting.getMin(), (int) numberSetting.getMax(), X + 205, posY + 15, 80, numberSetting);
                s.slider.draw(mouseX, mouseY);
                posY += 30;
            }
            if(s instanceof ModeSetting){
                ModeSetting modeSetting = (ModeSetting)s;
                Gui.drawRect(X + 124, posY2 + 5, X + 195, posY2 + 25, new Color(0, 0, 0, 205).getRGB());
                Gui.drawRect(X + 125, posY2 + 6, X + 194, posY2 + 24, new Color(26, 26, 26, 205).getRGB());
                font.drawString(s.name,X + 123, posY2, -1);
                font.drawString(modeSetting.getSelected(),X + 130, posY2 + 10, -1);
                posY2 += 30;
            }
            if(s instanceof BoolSetting){
                BoolSetting boolSetting = (BoolSetting)s;
                Gui.drawRect(X + 64, posY3 + 5, X + 120, posY3 + 25, new Color(0, 0, 0, 205).getRGB());
                Gui.drawRect(X + 65, posY3 + 6, X + 119, posY3 + 24, new Color(26, 26, 26, 205).getRGB());
                font.drawString(s.name,X + 63, posY3, -1);
                font.drawString("Enable",X + 70, posY3 + 10, -1);
                RenderUtils.drawRoundedRect(X + 105, posY3 + 12, 7, 7, 2, boolSetting.isEnable() ? color : new Color(0, 0, 0, 205).getRGB());
                posY3 += 30;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }

    @Override
    public void initGui() {
        super.initGui();
        dragging = false;
        if(ClickGui.cursor.isEnable()) {
            try {
                int min = org.lwjgl.input.Cursor.getMinCursorSize();
                IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
                Cursor emptyCursor = new Cursor(min, min, min / 2, min / 2, 1, tmp, null);
                Mouse.setNativeCursor(emptyCursor);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }



    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        try {
            Mouse.setNativeCursor(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void handleInput() throws IOException {
        super.handleInput();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }



    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2 && mouseY > y && mouseY < y2);
    }

    public ScaledResolution sr() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    public static FontManager getFont() {
        return font;
    }
}
