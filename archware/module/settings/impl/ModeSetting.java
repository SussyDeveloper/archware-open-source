package archware.module.settings.impl;

import archware.module.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {

    public String selected;
    public String equals;
    private List<String> modes = new ArrayList<String>();
    private int index;

    public ModeSetting(String name, String default_mode, String... options){
        this.name = name;
        this.modes = Arrays.asList(options);
        this.index = modes.indexOf(default_mode);
        this.selected = modes.get(index);
    }

    public boolean equals(String equ){
        return equ.equals(equals);
    }

    public boolean is(String mode) {
        return mode.equals(selected);
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
        index = modes.indexOf(selected);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        this.selected = modes.get(index);
    }

    public void setModes(List<String> modes) {
        this.modes = modes;
    }

    public List<String> getModes() {
        return modes;
    }

    public void cycle() {
        if (index < modes.size() - 1) {
            index++;
            selected = modes.get(index);
        } else if (index >= modes.size() - 1) {
            index = 0;
            selected = modes.get(0);
        }
    }
}
