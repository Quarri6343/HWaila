package com.quarri6343.hwaila;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.quarri6343.hwaila.util.CustomHUDUtil;

import javax.annotation.Nonnull;

public class HWaila extends JavaPlugin {

    public static final String HUD_IDENTIFIER = "waila";

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static HWaila instance;

    private ComponentType<EntityStore, WailaTargetComponent> wailaTargetComponentType;
    private Config<WailaConfig> config = this.withConfig("Waila", WailaConfig.CODEC);;

    public static HWaila getInstance() {
        return instance;
    }

    public ComponentType<EntityStore, WailaTargetComponent> getWailaTargetComponentType() {
        return this.wailaTargetComponentType;
    }

    public HWaila(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().atInfo().log(getDataDirectory().toAbsolutePath().toString());
    }

    public Config<WailaConfig> getConfig() {
        return config;
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        this.getCommandRegistry().registerCommand(new ToggleTooltipsCommand(this.getName(), this.getManifest().getVersion().toString()));

        wailaTargetComponentType = this.getEntityStoreRegistry().registerComponent(WailaTargetComponent.class, WailaTargetComponent::new);
        getEntityStoreRegistry().registerSystem(new WailaTickSystem(wailaTargetComponentType));

        getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Ref<EntityStore> ref = event.getPlayerRef();
            perPlayerSetup(ref);
        });
    }

    @Override
    protected void start() {
        for(World world : Universe.get().getWorlds().values()) {
            world.execute(() -> {
                for (PlayerRef player : world.getPlayerRefs()) {
                    Ref<EntityStore> ref = player.getReference();
                    if (ref != null) {
                        perPlayerSetup(ref);
                    }
                }
            });
        }
    }

    @Override
    protected void shutdown() {
        //TODO: find a way to remove HUD before the hud component is unregistered
    }

    private void perPlayerSetup(Ref<EntityStore> ref) {
        Player player = ref.getStore().getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
        CustomHUDUtil.setCustomHUD(player, playerRef, HUD_IDENTIFIER, new Tooltips(playerRef));

        boolean isTooltipEnabled = !config.get().tooltipBlackList.contains(playerRef.getUuid());
        WailaTargetComponent component = ref.getStore().getComponent(ref, getWailaTargetComponentType());
        if (component == null) {
            ref.getStore().addComponent(ref, getWailaTargetComponentType(), new WailaTargetComponent(isTooltipEnabled));
        } else {
            component.setEnabled(isTooltipEnabled);
        }
    }

    @SuppressWarnings("unused")
    private void perPlayerShutdown(Ref<EntityStore> ref) {
        WailaTargetComponent component = ref.getStore().getComponent(ref, getWailaTargetComponentType());
        if (component != null) {
            component.setEnabled(false);
        }
    }
}