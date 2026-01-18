package com.quarri6343.hwaila;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.quarri6343.hwaila.util.CustomHUDUtil;

import javax.annotation.Nonnull;

import static com.quarri6343.hwaila.HWaila.HUD_IDENTIFIER;

/**
 * Toggle Waila Hud
 */
public class ToggleTooltipsCommand extends AbstractPlayerCommand {

    @Nonnull
    private final FlagArg showArg = this.withFlagArg("show", "show hud");
    @Nonnull
    private final FlagArg hideArg = this.withFlagArg("hide", "hide hud");

    public ToggleTooltipsCommand(String pluginName, String pluginVersion) {
        super("waila", "Show tooltips.");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert playerComponent != null;
        WailaTargetComponent targetComponent = store.getComponent(ref, WailaTargetComponent.getComponentType());
        assert targetComponent != null;

        toggleTooltips(context, playerRef, targetComponent, playerComponent);

        updateTooltipsConfig(playerRef, targetComponent);
    }

    private void toggleTooltips(CommandContext context, PlayerRef playerRef, WailaTargetComponent targetComponent, Player playerComponent) {
        if (this.hideArg.provided(context)) {
            targetComponent.setEnabled(false);
        } else if (this.showArg.provided(context)) {
            //in case the hud somehow disabled
            CustomHUDUtil.setCustomHUD(playerComponent, playerRef, HUD_IDENTIFIER, new Tooltips(playerRef));
            targetComponent.setEnabled(true);
        } else {
            targetComponent.setEnabled(!targetComponent.isEnabled());
        }
    }

    private static void updateTooltipsConfig(PlayerRef playerRef, WailaTargetComponent targetComponent) {
        Config<WailaConfig> config = HWaila.getInstance().getConfig();
        if (targetComponent.isEnabled()) {
            config.get().tooltipBlackList.remove(playerRef.getUuid());
        } else {
            config.get().tooltipBlackList.add(playerRef.getUuid());
        }
        config.save();
    }
}