package archware.ui.notification;

import java.awt.*;

public enum NotificationType {
    SUCCESS(new Color(64, 255, 0).getRGB()),
    WARNING(new Color(255, 242, 0).getRGB()),
    INFO(new Color(227, 227, 227).getRGB()),
    ERROR(new Color(255, 0, 0).getRGB());

    private int color;

    NotificationType(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
