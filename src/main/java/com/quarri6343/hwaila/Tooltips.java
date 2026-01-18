package com.quarri6343.hwaila;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.builtin.asseteditor.util.AssetPathUtil;
import com.hypixel.hytale.protocol.packets.interface_.CustomUICommand;
import com.hypixel.hytale.server.core.asset.AssetModule;
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
import com.quarri6343.hwaila.api.Accessor;
import com.quarri6343.hwaila.api.BlockAccessor;
import com.quarri6343.hwaila.api.EntityAccessor;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tooltips extends CustomUIHud {

    private static final Map<UUID, CustomUICommand[]> previousHUDCache = new HashMap<>();

    public void update(@Nonnull UICommandBuilder commandBuilder, Accessor accessor) {
        if (accessor instanceof EntityAccessor entityAccessor
            && entityAccessor.getEntity() instanceof NPCEntity npcEntity) {

            int roleIndex = npcEntity.getRoleIndex();
            String roleName = NPCPlugin.get().getName(roleIndex);
            String modelAssetID = npcEntity.toHolder().getComponent(ModelComponent.getComponentType()).getModel().getModelAssetId();
            String packName = ModelAsset.getAssetMap().getAssetPack(modelAssetID);

            commandBuilder.set("#Root.Visible", true);
            commandBuilder.set("#ItemIconContainer.Visible", false);
            commandBuilder.set("#NPCIcon.Visible", true);

            commandBuilder.set("#NPCIcon.Visible", doesNPCIconExist(roleName));
            commandBuilder.set("#NPCIcon.AssetPath", getNPCIconPath(roleName));

            //TODO:npc name i18n
            commandBuilder.set("#ItemNameLabel.Text", roleName);
            commandBuilder.set("#PackNameLabel.Text", packName);
        }
        else if (accessor instanceof BlockAccessor blockAccessor
                && blockAccessor.getBlockType() != null
                && blockAccessor.getBlockType().getItem() != null) {
            String itemID = blockAccessor.getBlockType().getItem().getId();

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
        }

        boolean isUISame = Arrays.equals(previousHUDCache.get(getPlayerRef().getUuid()), commandBuilder.getCommands());
        previousHUDCache.put(getPlayerRef().getUuid(), commandBuilder.getCommands());
        if (isUISame) {
            return; //don't send packet every frame
        }

        //never set clear flag to true (it will remove huds from other mods)
        super.update(false, commandBuilder);
    }

    public String getNPCIconPath(String roleName) {
        return "UI/Custom/Pages/Memories/npcs/" + roleName + ".png";
    }

    //TODO:cache
    public boolean doesNPCIconExist(String roleName) {
        for (AssetPack pack : AssetModule.get().getAssetPacks()) {
            Path fullPath = pack.getRoot().resolve(AssetPathUtil.DIR_COMMON).resolve(getNPCIconPath(roleName));
            if (Files.exists(fullPath)) {
                return true;
            }
        }
        return false;
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
