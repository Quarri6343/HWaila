package com.quarri6343.hwaila;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.quarri6343.hwaila.api.Accessor;

import javax.annotation.Nullable;

public class WailaTargetComponent implements Component<EntityStore> {

    private Accessor accessor;
    private Accessor previousAccessor;
    private boolean enabled = false;

    public static ComponentType<EntityStore, WailaTargetComponent> getComponentType() {
        return HWaila.getInstance().getWailaTargetComponentType();
    }

    public WailaTargetComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public WailaTargetComponent() {

    }

    public Accessor getAccessor() {
        return accessor;
    }

    public void setAccessor(Accessor accessor) {
        this.accessor = accessor;
    }

    public Accessor getPreviousAccessor() {
        return previousAccessor;
    }

    public void setPreviousAccessor(Accessor previousAccessor) {
        this.previousAccessor = previousAccessor;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Component<EntityStore> clone() {
        WailaTargetComponent clone = new WailaTargetComponent(this.enabled);
        clone.setPreviousAccessor(this.previousAccessor);
        clone.setAccessor(this.accessor);
        return clone;
    }
}
