package com.quarri6343.hwaila;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class WailaTargetComponent implements Component<EntityStore> {

    private @Nullable String itemId;

    public static ComponentType<EntityStore, WailaTargetComponent> getComponentType() {
        return HWaila.getInstance().getWailaTargetComponentType();
    }

    public WailaTargetComponent(@Nullable String itemId) {
        this.itemId = itemId;
    }

    @Nullable
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public Component<EntityStore> clone() {
        return new WailaTargetComponent(this.itemId);
    }
}
