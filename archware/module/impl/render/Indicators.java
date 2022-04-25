package archware.module.impl.render;

import archware.event.Event;
import archware.module.Category;
import archware.module.Module;

public class Indicators extends Module {



    public Indicators() {
        super("Indicators", "", 0, Category.RENDER);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
    }
}
