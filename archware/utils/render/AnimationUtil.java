package archware.utils.render;


public class AnimationUtil {
    public static float calculateCompensation(float target, float current, long delta, int speed) {
        float diff = current - target;
        if (delta < 1L)
            delta = 1L;
        if (diff > speed) {
            double xD = (((float)(speed * delta) / 16.0F) < 0.25D) ? 0.5D : ((float)(speed * delta) / 16.0F);
            current = (float)(current - xD);
            if (current < target)
                current = target;
        } else if (diff < -speed) {
            double xD = (((float)(speed * delta) / 16.0F) < 0.25D) ? 0.5D : ((float)(speed * delta) / 16.0F);
            current = (float)(current + xD);
            if (current > target)
                current = target;
        } else {
            current = target;
        }
        return current;
    }

    public static double animate(double target, double current, double speed) {
        boolean larger = (target > current);
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D)
            factor = 0.1D;
        if (larger) {
            current += factor;
        } else {
            current -= factor;
        }
        return current;
    }
}
