package archware.module.impl.combat;

import archware.Client;
import archware.event.Event;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;
import archware.module.settings.impl.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;

public class AntiBot extends Module {

    ModeSetting mode = new ModeSetting("Mode", "NPC", "NPC", "Advanced");
    ArrayList<Entity> bots = new ArrayList<>();
    static AntiBot instance = new AntiBot();

    public AntiBot() {
        super("AntiBot", "", 0, Category.COMBAT);
        addSettings(mode);
    }


    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventUpdate){
            KillAura aura = new KillAura();
            switch (mode.getSelected()){
                case "NPC":{
                    for (Entity entity : mc.theWorld.loadedEntityList){
                        if(entity.getName().contains("[NCP]") || entity.getName().contains(" ")){
                            bots.add(entity);
                        }
//                        Client.message(entity.getName() + "Is Bot: " + isBot(entity));
                    }
                    break;
                }
                case "Advanced":{
                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        bots.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        bots.clear();
    }

    public boolean isBot(Entity e){
        return bots.contains(e);
    }

    public static AntiBot getInstance() {
        return instance;
    }
}
