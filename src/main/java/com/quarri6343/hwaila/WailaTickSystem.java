package com.quarri6343.hwaila;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.quarri6343.hwaila.HWaila.HUD_IDENTIFIER;

public class WailaTickSystem extends EntityTickingSystem<EntityStore> {

    private final ComponentType<EntityStore, WailaTargetComponent> componentType;

    public WailaTickSystem(ComponentType<EntityStore, WailaTargetComponent> componentType) {
        this.componentType = componentType;
    }

    @Nonnull
    @Override
    public Query getQuery() {
//        return Query.and(Player.getComponentType()); don't work due to entitymodule depencency
        return Query.and(componentType);
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);
        WailaTargetComponent component = store.getComponent(playerRef, componentType);
        Player playerComponent = store.getComponent(playerRef, Player.getComponentType());
        assert playerComponent != null;
        if (!(CustomHUDUtil.getCustomHUD(playerComponent, HUD_IDENTIFIER) instanceof Tooltips tooltips)) {
            return;
        }

        if (component == null) {
            commandBuffer.addComponent(playerRef, componentType, component = new WailaTargetComponent(true));
        }
        if (!component.isEnabled()) {
            component.setItemId(null);
            component.setEntityRoleIndex(-1);
            tooltips.update(new UICommandBuilder(), component);
            return;
        }

        WailaRaycastSelector selector = new WailaRaycastSelector();
        Selector runtime = selector.newSelector();

        ModelComponent playerModelComponent = store.getComponent(playerRef, ModelComponent.getComponentType());
        if (playerModelComponent != null) {
            float offset = playerModelComponent.getModel().getEyeHeight(playerRef, store);
            selector.setOffset(new Vector3d(0, offset, 0));
        }

        runtime.tick(commandBuffer, playerRef, 0, 0);

        if (handleEntity(store, commandBuffer, component, runtime, playerRef)
            || handleBlock(store, commandBuffer, component, runtime, playerRef)) {
            tooltips.update(new UICommandBuilder(), component);
        }
    }

    //TODO: BlockAccessorHandler, EntityAccessorHandler
    private static boolean handleBlock(Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, WailaTargetComponent component, Selector runtime, Ref<EntityStore> playerRef) {
        String oldID = component.getItemId();
        component.setItemId(null);
        runtime.selectTargetBlocks(commandBuffer, playerRef, (x, y, z) -> {
            World world = commandBuffer.getExternalData().getWorld();
            ChunkStore chunkStore = world.getChunkStore();
            TransformComponent transformComponent = store.getComponent(playerRef, TransformComponent.getComponentType());
            WorldChunk chunk = chunkStore.getStore()
                    .getComponent(transformComponent.getChunkRef(), WorldChunk.getComponentType());
            BlockType blockType = chunk.getBlockType(x, y, z);

            if (blockType != null && blockType.getItem() != null) {
                String itemID = blockType.getItem().getId();
                component.setItemId(itemID);

                String blockKey = (String) blockType.getData().getKey();
                String packName = AssetRegistry.getAssetStore(BlockType.class).getAssetMap().getAssetPack(blockKey);
                component.setPluginName(packName);
            }
        });

        return !Objects.equals(component.getItemId(), oldID);
    }

    private static boolean handleEntity(Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, WailaTargetComponent component, Selector runtime, Ref<EntityStore> playerRef) {
        int oldRoleIndex = component.getEntityRoleIndex();
        component.setEntityRoleIndex(-1);
        runtime.selectTargetEntities(commandBuffer, playerRef, ((entityRef, vector4d) -> {
            NPCEntity npcEntity = store.getComponent(entityRef, NPCEntity.getComponentType());
            //TODO: support player entity
            int roleIndex = -1;
            String packName = null;
            if (npcEntity != null) {
                roleIndex = npcEntity.getRoleIndex();
                packName = ModelAsset.getAssetMap().getAssetPack(npcEntity.getRoleName());
            }
            component.setEntityRoleIndex(roleIndex);
            component.setPluginName(packName);
        }), _ -> true);

        return component.getEntityRoleIndex() != oldRoleIndex;
    }
}
