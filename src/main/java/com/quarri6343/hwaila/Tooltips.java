package com.quarri6343.hwaila;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.npc.NPCPlugin;

import javax.annotation.Nonnull;

public class Tooltips extends CustomUIHud {

    public void update(@Nonnull UICommandBuilder commandBuilder, WailaTargetComponent targetComponent) {
        String itemID = targetComponent.getItemId();
        int roleIndex = targetComponent.getEntityRoleIndex();
        String roleName = NPCPlugin.get().getName(roleIndex);

        if (roleName != null) {
            commandBuilder.set("#Root.Visible", true);
            //TODO:npc name i18n
            commandBuilder.set("#ItemNameLabel.Text", roleName);
            String pluginName = targetComponent.getPluginName();
            commandBuilder.set("#PackNameLabel.Text", pluginName);
        }
        else if(itemID != null) {
            commandBuilder.set("#Root.Visible", true);
            ItemStack itemStack = new ItemStack(itemID, 10, null);
            String itemSelector = "#ItemIconContainer[0] ";
            commandBuilder.append("#ItemIconContainer", "Pages/DroppedItemSlot.ui");
            commandBuilder.set(itemSelector + "#ItemIcon.ItemId", itemStack.getItemId());
            commandBuilder.set(itemSelector + "#ItemIcon.Quantity", itemStack.getQuantity());

            String lang = getPlayerRef().getLanguage();
            String itemName = I18nModule.get().getMessage(lang, itemStack.getItem().getTranslationKey());
            if (itemName != null) {
                commandBuilder.set("#ItemNameLabel.Text", itemName);
            }
            //TODO: Fallback behaviour when item name is null
            String pluginName = targetComponent.getPluginName();
            if (pluginName != null) {
                commandBuilder.set("#PackNameLabel.Text", pluginName);
            }
        }
        else {
            commandBuilder.set("#Root.Visible", false);
        }

        //never set clear flag to true (it will remove huds from other mods)
        super.update(false, commandBuilder);
    }

    public Tooltips(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commandBuilder) {
        commandBuilder.append("Pages/Tooltips.ui");
    }
}
