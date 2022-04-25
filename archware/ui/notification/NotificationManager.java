package archware.ui.notification;

import archware.utils.Wrapper;
import archware.utils.font.FontManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import archware.utils.render.AnimationUtil;
import archware.utils.render.RenderUtils;
import archware.utils.render.Translate;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager{

    private static final List<Notification> NOTIFICATIONS = new CopyOnWriteArrayList<>();
    private static final FontManager iconfont = new FontManager("nicon", Font.PLAIN, 42, 1, 1);

    public static void render(ScaledResolution sr) {
        String icon;
        if (NOTIFICATIONS.isEmpty())
            return;
        int srScaledHeight = sr.getScaledHeight();
        int scaledWidth = sr.getScaledWidth();
        int y = srScaledHeight - 38;
        for (Notification notification : NOTIFICATIONS) {
            Translate translate = notification.getTranslate();
            int width = notification.getWidth();
            if (!notification.getTimer().elapsed(notification.getTime())) {
                notification.posX = AnimationUtil.animate(width, notification.posX, 0.08D);
                translate.interpolate((scaledWidth - width), y, 0.1D);
            } else {
                notification.posX = AnimationUtil.animate(0.0D, notification.posX, 0.08D);
                if (notification.posX < 1.0D)
                    NOTIFICATIONS.remove(notification);
                y += 35;
            }

            float translateX = (float) translate.getX();
            float translateY = (float) translate.getY();
//            GL11.glPushMatrix();
            RenderUtils.drawRoundedRect((scaledWidth - notification.posX), translateY + 7.0F, scaledWidth + 25, 30,0, new Color(0, 0, 0, 195).getRGB());
            RenderUtils.drawRoundedRect((scaledWidth - notification.posX), translateY + 35F, width *
                    (float)(notification.getTime() - notification.getTimer().getElapsedTime()) / notification.getTime(), 2,0, notification.getType().getColor());
            Wrapper.getTitlefont().drawString(notification.getTitle(), (float) (scaledWidth - notification.posX) + 30, translateY + 10, -1);
            Wrapper.getInfofont().drawString(ChatFormatting.GRAY + notification.getContent(), (float) (scaledWidth - notification.posX) + 30, translateY + 22, -1);
            switch (notification.getType()){
                case SUCCESS: {
                    icon = "o";
                    iconfont.drawString(icon, (float) (scaledWidth - notification.posX) + 5, translateY + 10, notification.getType().getColor());
                    break;
                }
                case WARNING:{
                    icon = "r";
                    iconfont.drawString(icon, (float) (scaledWidth - notification.posX) + 5, translateY + 10, notification.getType().getColor());
                    break;
                }
                case INFO:{
                    icon = "m";
                    iconfont.drawString(icon, (float) (scaledWidth - notification.posX) + 6, translateY + 10, notification.getType().getColor());
                    break;
                }
                case ERROR:{
                    icon = "r";
                    iconfont.drawString(icon, (float) (scaledWidth - notification.posX) + 5, translateY + 10, notification.getType().getColor());
                    break;
                }
            }
//            GL11.glPopMatrix();
            y -= 35;
        }
    }

    public static void queue(String title, String content, NotificationType type, int ms) {
        FontManager trueTypeFontRenderer1 = Wrapper.getTitlefont();
        FontManager trueTypeFontRenderer2 = Wrapper.getInfofont();
        NOTIFICATIONS.add(new Notification(title, content, type, trueTypeFontRenderer1, trueTypeFontRenderer2, ms));
    }


}
