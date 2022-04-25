package archware.module.settings.impl;

import archware.module.settings.Setting;

public class BoolSetting extends Setting {

    boolean enable;

    public BoolSetting(String name, boolean enable){
        this.name = name;
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void toggle(){
        enable = !enable;
    }
}
