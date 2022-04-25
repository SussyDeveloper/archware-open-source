package archware.ui.notification;

import archware.utils.font.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import archware.utils.render.Translate;
import archware.utils.timers.Stopwatch;

public class Notification {

    public String title;
    public String content;
    public Translate translate;
    public float width;
    public float height;
    public int time;
    public long duration;
    public Stopwatch timer;
    public FontManager fonttitle;
    public FontManager fontinfo;
    public NotificationType type;
    public double posX;

    public Notification(String title, String content, NotificationType type, FontManager titleFont, FontManager infoFont, int ms) {
        this.title = title;
        this.content = content;
        this.time = ms;
        this.type = type;
        this.timer = new Stopwatch();
        this.fonttitle = titleFont;
        this.fontinfo = infoFont;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.translate = new Translate((sr.getScaledWidth() - getWidth()), (sr.getScaledHeight() + 2));
    }

    public Translate getTranslate() {
        return translate;
    }

    public final int getWidth() {
        return (int)Math.max(100.0F, Math.max(fonttitle.getWidth(this.title), this.fontinfo.getWidth(this.content)) + 35);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getTime() {
        return time;
    }

    public long getDuration() {
        return duration;
    }

    public Stopwatch getTimer() {
        return timer;
    }

    public NotificationType getType() {
        return type;
    }
}
