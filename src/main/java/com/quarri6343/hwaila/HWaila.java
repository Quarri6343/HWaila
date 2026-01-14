package com.quarri6343.hwaila;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class HWaila extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private ComponentType<EntityStore, WailaTargetComponent> wailaTargetComponentType;

    private static HWaila instance;

    public static HWaila getInstance() {
        return instance;
    }

    public ComponentType<EntityStore, WailaTargetComponent> getWailaTargetComponentType() {
        return this.wailaTargetComponentType;
    }

    public HWaila(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        this.getCommandRegistry().registerCommand(new ShowTooltipsCommand(this.getName(), this.getManifest().getVersion().toString()));

        wailaTargetComponentType = this.getEntityStoreRegistry().registerComponent(WailaTargetComponent.class, WailaTargetComponent::new);
        getEntityStoreRegistry().registerSystem(new PlayerTickEventSystem(wailaTargetComponentType));
    }
}