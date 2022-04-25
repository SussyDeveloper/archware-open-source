package archware.module.settings;

import net.minecraft.client.gui.Gui;

import org.lwjgl.input.Mouse;
import archware.module.impl.render.ClickGui;
import archware.module.settings.impl.NumberSetting;

import java.awt.*;

public class Slider {
    int min;
    int max;
    int x;
    int y;
    int width;
    int mouseX;
    int mouseY;
    public boolean dragging = isSliderHovered(mouseX, mouseY) ? Mouse.isButtonDown(0) ? true : false : false;
    NumberSetting option;

    public Slider(int min, int max, int x, int y, int width, NumberSetting option) {
        this.min = min;
        this.max = max;
        this.x = x;
        this.y = y;
        this.width = width;
        this.option = option;
    }


    public void draw(int mouseX, int mouseY) {
        int color = (new Color((int) ClickGui.red.getValue(), (int) ClickGui.green.getValue(), (int) ClickGui.blue.getValue())).getRGB();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        double percentBar = (option.getValue() - min)/(max - min);
        Gui.drawRect(x, y - 2f, x + width, (float) (y + 4f), new Color(0, 0, 0, 205).getRGB());
        Gui.drawRect(x, y - 2f, (float)(x + (percentBar * width)), y + 4f, color);

        boolean dragging1 = isSliderHovered(mouseX, mouseY) && Mouse.isButtonDown(0);
        if(dragging1) {
            if(option.isOnlyInt()){
                option.value = Math.round(option.getMin() + ((mouseX - ((float)x)) / ((float) width) * (option.getMax() - option.getMin())));
            }else{
                option.value = option.getMin() + ((mouseX - ((float)x)) / ((float) width) * (option.getMax() - option.getMin()));
            }
        }
    }

    public boolean isSliderHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y - 2 && mouseY <= y + 3;
    }
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void mouseReleased() {
        this.dragging = false;
    }
}
