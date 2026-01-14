package com.quarri6343.hwaila;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class Tooltips extends CustomUIHud {

    public void update(boolean clear, @Nonnull UICommandBuilder commandBuilder, WailaTargetComponent targetComponent) {
        String itemID = targetComponent.getItemId();
        if (itemID == null) {
            super.update(clear, commandBuilder);
            return;
        }

        commandBuilder.append("Pages/Tooltips.ui");

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

        super.update(clear, commandBuilder);
    }

    public Tooltips(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commandBuilder) {
    }
}
