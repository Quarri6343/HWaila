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

import javax.annotation.Nonnull;

/**
 * Show Test Waila Hud
 */
public class ShowTooltipsCommand extends AbstractPlayerCommand {

    @Nonnull
    private final FlagArg hideArg = this.withFlagArg("hide", "hide hud");

    public ShowTooltipsCommand(String pluginName, String pluginVersion) {
        super("waila", "Show tooltips.");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());

        assert playerComponent != null;

        HudManager hudManager = playerComponent.getHudManager();
        if (this.hideArg.provided(context)) {
            hudManager.setCustomHud(playerRef, null);
        } else {
            hudManager.setCustomHud(playerRef, new Tooltips(playerRef));
        }
    }
}