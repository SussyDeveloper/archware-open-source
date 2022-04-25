package archware.module;

import net.minecraft.client.Minecraft;
import archware.event.Event;
import archware.module.settings.Setting;
import archware.utils.render.Translate;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

public class Module {

    String name, description, displayname;
    int key;
    Category category;
    boolean isEnabled, isHidden;

    ScaledResolution sr = new ScaledResolution(mc);

    public Translate translate = new Translate(sr.getScaledWidth(), 0.0D);

    public List<Setting> settings = new ArrayList<>();

    public static Minecraft mc = Minecraft.getMinecraft();

    public Module(String name, String description, int key, Category category){
        this.name = name;
        this.description = description;
        this.key = key;
        this.category = category;
    }

    public void toggle(){
        isEnabled = !isEnabled;
        if(isEnabled){
            onEnable();
        }else{
            onDisable();
        }
    }

    public void onEvent(Event event){

    }

    public void onEnable(){

    }

    public void onDisable(){

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getDisplayName() {
        return (this.displayname != null) ? this.displayname : this.name;
    }

    public void setDisplayName(String displayName) {
        this.displayname = displayName;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    private void addSetting(Setting setting) {
        getSettings().add(setting);
    }

    public void addSettings(Setting... settings) {
        for (Setting setting : settings) {
            addSetting(setting);
        }
    }

    public Translate getTranslate() {
        return this.translate;
    }
}
