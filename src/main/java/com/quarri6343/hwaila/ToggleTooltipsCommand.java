package com.quarri6343.hwaila;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;

/**
 * Toggle Test Waila Hud
 */
public class ToggleTooltipsCommand extends AbstractPlayerCommand {

    @Nonnull
    private final FlagArg showArg = this.withFlagArg("show", "show hud");
    @Nonnull
    private final FlagArg hideArg = this.withFlagArg("hide", "hide hud");

    public ToggleTooltipsCommand(String pluginName, String pluginVersion) {
        super("waila", "Show tooltips.");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert playerComponent != null;
        WailaTargetComponent targetComponent = store.getComponent(ref, WailaTargetComponent.getComponentType());
        assert targetComponent != null;

        // the game crashes when you remove the hud through hudManager.resetHud(playerRef);
        if (this.hideArg.provided(context)) {
            targetComponent.setEnabled(false);
        } else if (this.showArg.provided(context)){
            //in case other mod override the hud
            HudManager hudManager = playerComponent.getHudManager();
            hudManager.setCustomHud(playerRef, new Tooltips(playerRef));
            targetComponent.setEnabled(true);
        }
        else {
            targetComponent.setEnabled(!targetComponent.isEnabled());
        }

        Config<WailaConfig> config = HWaila.getInstance().getConfig();
        if(targetComponent.isEnabled()) {
            config.get().tooltipBlackList.remove(playerRef.getUuid());
        }
        else {
            config.get().tooltipBlackList.add(playerRef.getUuid());
        }
        config.save();
    }
}