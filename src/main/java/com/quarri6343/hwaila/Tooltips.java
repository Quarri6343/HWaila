package com.quarri6343.hwaila;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.quarri6343.hwaila.api.BlockAccessor;
import com.quarri6343.hwaila.api.EntityAccessor;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Tooltips extends CustomUIHud {

    public void update(@Nonnull UICommandBuilder commandBuilder, WailaTargetComponent targetComponent) {
        //TODO: accessor null handling
        if (targetComponent.getAccessor() instanceof EntityAccessor entityAccessor
            && entityAccessor.getEntity() instanceof NPCEntity npcEntity) {

            int roleIndex = npcEntity.getRoleIndex();
            String roleName = NPCPlugin.get().getName(roleIndex);
            String modelAssetID = npcEntity.toHolder().getComponent(ModelComponent.getComponentType()).getModel().getModelAssetId();
            String packName = ModelAsset.getAssetMap().getAssetPack(modelAssetID);
            // TODO: move same target check to provider
            if (targetComponent.getPreviousAccessor() instanceof EntityAccessor previousAccessor
                    && previousAccessor.getEntity() instanceof NPCEntity previousEntity
                    && previousEntity.getRoleIndex() == roleIndex) {
                return;
            }

            commandBuilder.set("#Root.Visible", true);
            commandBuilder.set("#ItemIconContainer.Visible", false);
            commandBuilder.set("#NPCIcon.Visible", true);

            commandBuilder.set("#NPCIcon.AssetPath", getNPCIconPath(roleName));

            //TODO:npc name i18n
            commandBuilder.set("#ItemNameLabel.Text", roleName);
            commandBuilder.set("#PackNameLabel.Text", packName);
        }
        else if (targetComponent.getAccessor() instanceof BlockAccessor blockAccessor
                && blockAccessor.getBlockType() != null
                && blockAccessor.getBlockType().getItem() != null) {
            String itemID = blockAccessor.getBlockType().getItem().getId();

            // TODO: move same target check to provider
            if (targetComponent.getPreviousAccessor() instanceof BlockAccessor previousAccessor
                    && Objects.equals(previousAccessor.getBlockType(),blockAccessor.getBlockType())) {
                return;
            }

            String blockKey = (String) blockAccessor.getBlockType().getData().getKey();
            String packName = AssetRegistry.getAssetStore(BlockType.class).getAssetMap().getAssetPack(blockKey);

            commandBuilder.set("#Root.Visible", true);
            commandBuilder.set("#ItemIconContainer.Visible", true);
            commandBuilder.set("#NPCIcon.Visible", false);

            ItemStack itemStack = new ItemStack(itemID, 10, null);
            String itemSelector = "#ItemIconContainer[0] ";
            commandBuilder.set(itemSelector + "#ItemIcon.ItemId", itemStack.getItemId());
            commandBuilder.set(itemSelector + "#ItemIcon.Quantity", itemStack.getQuantity());

            String lang = getPlayerRef().getLanguage();
            String itemName = I18nModule.get().getMessage(lang, itemStack.getItem().getTranslationKey());
            if (itemName != null) {
                commandBuilder.set("#ItemNameLabel.Text", itemName);
            }
            //TODO: Fallback behaviour when item name is null
            if (packName != null) {
                commandBuilder.set("#PackNameLabel.Text", packName);
            }
        }
        else {
            commandBuilder.set("#Root.Visible", false);

            if (targetComponent.getPreviousAccessor() == null) {
                return;
            }
        }

        //never set clear flag to true (it will remove huds from other mods)
        super.update(false, commandBuilder);
    }

    //TODO: do not display icon when it is null
    public String getNPCIconPath(String roleName) {
        return "UI/Custom/Pages/Memories/npcs/" + roleName + ".png";
    }

    public Tooltips(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commandBuilder) {
        commandBuilder.append("Pages/Tooltips.ui");
        commandBuilder.append("#ItemIconContainer", "Pages/DroppedItemSlot.ui");
    }
}
