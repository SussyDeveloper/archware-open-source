package archware.module.impl.movement;

import archware.event.Event;
import archware.event.impl.EventUpdate;
import archware.module.Category;
import archware.module.Module;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", "It automatically sprints", 0, Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.setSprinting(false);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        if(event instanceof EventUpdate){
            if(!mc.thePlayer.isCollidedHorizontally && mc.thePlayer.moveForward > 0){
                mc.thePlayer.setSprinting(true);
            }
        }
    }
}
