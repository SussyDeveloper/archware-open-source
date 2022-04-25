package archware.module;

import archware.module.impl.combat.*;
import archware.module.impl.movement.*;
import archware.module.impl.other.*;
import archware.module.impl.player.AntiFall;
import archware.module.impl.player.AutoArmor;
import archware.module.impl.player.ChestStealer;
import archware.module.impl.player.InventoryCleaner;
import archware.module.impl.render.*;

import java.util.ArrayList;

public class ModuleManager {

    ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager(){
        modules.add(new ESP());
        modules.add(new TargetStrafe());
        modules.add(new Chams());
        modules.add(new TargetHUD());
        modules.add(new HUD());
        modules.add(new ClickGui());
        modules.add(new KillAura());
        modules.add(new Speed());
        modules.add(new Sprint());
        modules.add(new Flight());
        modules.add(new TimeChanger());
        modules.add(new NoFall());
        modules.add(new AntiFall());
        modules.add(new Animations());
        modules.add(new ChestStealer());
        modules.add(new InventoryCleaner());
        modules.add(new AutoArmor());
        modules.add(new Indicators());
        modules.add(new Bypass());
        modules.add(new HackerDetector());
        modules.add(new Step());
        modules.add(new AutoPot());
        modules.add(new Velocity());
        modules.add(new NoSlow());
        modules.add(new InvMove());
        modules.add(new Scaffold());
        modules.add(new Criticals());
        modules.add(new LagDetector());
        modules.add(new AutoPlay());
        modules.add(new AntiBot());
        modules.add(new AntiAim());
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public ArrayList<Module> getModulesByCategory(Category category) {
        ArrayList<Module> modulesByCategory = new ArrayList<>();
        getModules().forEach(module -> {
            if (module.category == category)
                modulesByCategory.add(module);
        });
        return modulesByCategory;
    }

    public void toggleByKey(int key) {
        this.modules.forEach(module -> {
            if (module.getKey() == key)
                module.toggle();
        });
    }
    public Module getModuleByName(String name) {
        for (Module m : getModules()) {
            if (m.name.equalsIgnoreCase(name))
                return m;
        }
        return null;
    }


}
