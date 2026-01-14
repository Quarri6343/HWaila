package com.quarri6343.hwaila;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PlayerTickEventSystem extends EntityTickingSystem<EntityStore> {

    private final ComponentType<EntityStore, WailaTargetComponent> componentType;

    public PlayerTickEventSystem(ComponentType<EntityStore, WailaTargetComponent> componentType) {
        this.componentType = componentType;
    }

    @Nonnull
    @Override
    public Query getQuery() {
        return Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        WailaTargetComponent component = archetypeChunk.getComponent(index, componentType);
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);
        Player playerComponent = store.getComponent(playerRef, Player.getComponentType());
        assert playerComponent != null;
        HudManager hudManager = playerComponent.getHudManager();

        if (component == null) {
            commandBuffer.addComponent(playerRef, componentType, component = new WailaTargetComponent(null, true));
        }
        if (!component.isEnabled()) {
            component.setItemId(null);
            if (hudManager.getCustomHud() instanceof Tooltips tooltips) {
                tooltips.update(true, new UICommandBuilder(), component);
            }
            return;
        }

        WailaRaycastSelector selector = new WailaRaycastSelector();
        Selector runtime = selector.newSelector();

        ModelComponent playerModelComponent = store.getComponent(playerRef, ModelComponent.getComponentType());
        if (playerModelComponent != null) {
            float offset = playerModelComponent.getModel().getEyeHeight(playerRef, store);
            //mismatch protocol v3d vs math v3d
            selector.setOffset(new Vector3d(0, offset, 0));
        }

        runtime.tick(commandBuffer, playerRef, 0, 0);

        final WailaTargetComponent oldComponent = ((WailaTargetComponent) component.clone());
        final WailaTargetComponent newComponent = component;
        component.setItemId(null);

        runtime.selectTargetBlocks(commandBuffer, playerRef, (x, y, z) -> {
            World world = commandBuffer.getExternalData().getWorld();
            ChunkStore chunkStore = world.getChunkStore();
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
            WorldChunk chunk = chunkStore.getStore()
                    .getComponent(transformComponent.getChunkRef(), WorldChunk.getComponentType());
            BlockType blockType = chunk.getBlockType(x, y, z);

            if (blockType != null && blockType.getItem() != null) {
                String itemID = blockType.getItem().getId();
                newComponent.setItemId(itemID);
            }
        });

        if (hudManager.getCustomHud() instanceof Tooltips tooltips
            && !Objects.equals(oldComponent.getItemId(), newComponent.getItemId())) {
            tooltips.update(true, new UICommandBuilder(), newComponent);
        }
    }
}
