package com.quarri6343.hwaila;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class WailaTargetComponent implements Component<EntityStore> {

    private @Nullable String itemId;
    private int entityRoleIndex;
    private String pluginName;
    private boolean enabled = false;

    public static ComponentType<EntityStore, WailaTargetComponent> getComponentType() {
        return HWaila.getInstance().getWailaTargetComponentType();
    }

    public WailaTargetComponent(boolean enabled) {
        this.enabled = enabled;
    }

    public WailaTargetComponent() {

    }

    @Nullable
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getEntityRoleIndex() {
        return entityRoleIndex;
    }

    public void setEntityRoleIndex(int entityRoleIndex) {
        this.entityRoleIndex = entityRoleIndex;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
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
        clone.setItemId(this.itemId);
        clone.setEntityRoleIndex(this.entityRoleIndex);
        return clone;
    }
}
