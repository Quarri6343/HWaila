package com.quarri6343.hwaila;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class HWaila extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private ComponentType<EntityStore, WailaTargetComponent> wailaTargetComponentType;

    private Config<WailaConfig> config = this.withConfig("Waila", WailaConfig.CODEC);;

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
        getLogger().atInfo().log(getDataDirectory().toAbsolutePath().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        this.getCommandRegistry().registerCommand(new ToggleTooltipsCommand(this.getName(), this.getManifest().getVersion().toString()));

        wailaTargetComponentType = this.getEntityStoreRegistry().registerComponent(WailaTargetComponent.class, WailaTargetComponent::new);
        getEntityStoreRegistry().registerSystem(new PlayerTickEventSystem(wailaTargetComponentType));

        getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Ref<EntityStore> ref = event.getPlayerRef();

            PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            HudManager hudManager = player.getHudManager();
            hudManager.setCustomHud(playerRef, new Tooltips(playerRef));

            boolean isTooltipEnabled = !config.get().tooltipBlackList.contains(playerRef.getUuid());
            if (ref.getStore().getComponent(ref, getWailaTargetComponentType()) == null) {
                ref.getStore().addComponent(ref, getWailaTargetComponentType(), new WailaTargetComponent(null, isTooltipEnabled));
            } else {
                ref.getStore().getComponent(ref, getWailaTargetComponentType()).setEnabled(isTooltipEnabled);
            }
        });
    }

    public Config<WailaConfig> getConfig() {
        return config;
    }
}