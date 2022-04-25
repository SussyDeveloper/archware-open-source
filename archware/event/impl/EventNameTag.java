package archware.event.impl;

import net.minecraft.entity.EntityLivingBase;
import archware.event.Event;

public class EventNameTag extends Event {

    private final EntityLivingBase entityLivingBase;

    public EventNameTag(EntityLivingBase entityLivingBase) {
        this.entityLivingBase = entityLivingBase;
    }

    public EntityLivingBase getEntityLivingBase() {
        return entityLivingBase;
    }
}
