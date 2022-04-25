package archware.utils.render;

import net.minecraft.client.Minecraft;

import java.math.BigDecimal;
import java.math.MathContext;

public final class Translate {
    private double x;

    private double y;

    private long lastMS;

    public Translate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void animate(double newX, double newY) {
        this.x = RenderUtils.progressiveAnimation(this.x, newX, 1.0D);
        this.y = RenderUtils.progressiveAnimation(this.y, newY, 1.0D);
    }

    public void animate(double newX, double newY, double speed) {
        this.x = RenderUtils.progressiveAnimation(this.x, newX, speed);
        this.y = RenderUtils.progressiveAnimation(this.y, newY, speed);
    }

    public void translate(float targetX, float targetY) {
        this.x = (float)anim(this.x, targetX, 1.0D);
        this.y = (float)anim(this.y, targetY, 1.0D);
    }

    public void interpolate(float targetX, float targetY, float smoothing) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        int deltaX = (int)(Math.abs(targetX - this.x) * smoothing);
        int deltaY = (int)(Math.abs(targetY - this.y) * smoothing);
        this.x = AnimationUtil.calculateCompensation(targetX, (float)this.x, delta, deltaX);
        this.y = AnimationUtil.calculateCompensation(targetY, (float)this.y, delta, deltaY);
    }

    public final void interpolate(double targetX, double targetY, double smoothing) {
        this.x = AnimationUtil.animate(targetX, this.x, smoothing);
        this.y = AnimationUtil.animate(targetY, this.y, smoothing);
    }

    public static double anim(double now, double desired, double speed) {
        double dif = Math.abs(now - desired);
        int fps = Minecraft.getDebugFPS();
        if (dif > 0.0D) {
            double animationSpeed = roundToDecimalPlace(Math.min(10.0D, Math.max(0.05D, 144.0D / fps * dif / 10.0D * speed)), 0.05D);
            if (dif != 0.0D && dif < animationSpeed)
                animationSpeed = dif;
            if (now < desired)
                return now + animationSpeed;
            if (now > desired)
                return now - animationSpeed;
        }
        return now;
    }

    public static double roundToDecimalPlace(double value, double inc) {
        double halfOfInc = inc / 2.0D;
        double floored = StrictMath.floor(value / inc) * inc;
        if (value >= floored + halfOfInc)
            return (new BigDecimal(StrictMath.ceil(value / inc) * inc, MathContext.DECIMAL64)).stripTrailingZeros().doubleValue();
        return (new BigDecimal(floored, MathContext.DECIMAL64)).stripTrailingZeros().doubleValue();
    }

    public double getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
