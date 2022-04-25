package archware.module.settings.impl;

import archware.module.settings.Setting;

public class NumberSetting extends Setting {

    public double value, min, max, inc;
    boolean onlyInt;

    public NumberSetting(String name, double value, double minimum, double maximum, double add, boolean onlyInt){
        this.name = name;
        this.value = value;
        this.min = minimum;
        this.max = maximum;
        this.inc = add;
        this.onlyInt = onlyInt;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void inc(boolean bool){
        this.value = (double) (getValue() + (bool ? 1 : -1) * inc);
    }

    public double getInc() {
        return inc;
    }

    public void setInc(double inc) {
        this.inc = inc;
    }

    public boolean isOnlyInt() {
        return onlyInt;
    }
}
