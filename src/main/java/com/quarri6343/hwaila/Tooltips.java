package com.quarri6343.hwaila;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class Tooltips extends CustomUIHud {

    @Nonnull
    private final WailaTargetComponent component;

    @Override
    public void update(boolean clear, @Nonnull UICommandBuilder commandBuilder) {
        String itemID = component.getItemId();
        if (itemID == null) {
            HWaila.getInstance().getLogger().atInfo().log("itemID == null");
            return; //TODO: hide hud
        }

        HWaila.getInstance().getLogger().atInfo().log(itemID);

        commandBuilder.append("Pages/Tooltips.ui");

        ItemStack itemStack = new ItemStack(itemID, 10, null);
        String itemSelector = "#ItemIconContainer[0] ";
        commandBuilder.append("#ItemIconContainer", "Pages/DroppedItemSlot.ui");
        commandBuilder.set(itemSelector + "#ItemIcon.ItemId", itemStack.getItemId());
        commandBuilder.set(itemSelector + "#ItemIcon.Quantity", itemStack.getQuantity());

        super.update(clear, commandBuilder);
    }

    public Tooltips(@Nonnull PlayerRef playerRef, WailaTargetComponent component) {
        super(playerRef);
        this.component = component;
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commandBuilder) {
        commandBuilder.append("Pages/Tooltips.ui");

        ItemStack itemStack = new ItemStack("Furniture_Crude_Chest_Small", 10, null);
        String itemSelector = "#ItemIconContainer[0] ";
        commandBuilder.append("#ItemIconContainer", "Pages/DroppedItemSlot.ui");
        commandBuilder.set(itemSelector + "#ItemIcon.ItemId", itemStack.getItemId());
        commandBuilder.set(itemSelector + "#ItemIcon.Quantity", itemStack.getQuantity());
    }
}
