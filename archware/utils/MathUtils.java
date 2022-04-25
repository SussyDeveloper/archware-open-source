package archware.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtils {

    private MathUtils() {
    }

    public static double roundToDecimalPlace(double value, double inc) {
        final double halfOfInc = inc / 2.0D;
        final double floored = Math.floor(value / inc) * inc;
        if (value >= floored + halfOfInc)
            return new BigDecimal(Math.ceil(value / inc) * inc, MathContext.DECIMAL64).
                    stripTrailingZeros()
                    .doubleValue();
        else
            return new BigDecimal(floored, MathContext.DECIMAL64)
                    .stripTrailingZeros()
                    .doubleValue();
    }

    public static double getRandomInRange(final double min, final double max) {
        final Random random = new Random();
        final double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;
        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static double randomNumber(final double max, final double min) {
        return Math.random() * (max - min) + min;
    }

    public static double square(double squareMe) {
        squareMe *= squareMe;
        return squareMe;
    }

    public static double round(final double num, final double increment) {
        if (increment < 0.0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(num);
        bd = bd.setScale((int)increment, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float range(float min, float max) {
        return min + (new Random().nextFloat() * (max - min));
    }

    public static double range(double min, double max) {
        return min + (new Random().nextDouble() * (max - min));
    }

    public static int range(int min, int max) {
        return min + (new Random().nextInt() * (max - min));
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }
}
