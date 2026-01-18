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
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.quarri6343.hwaila.util.CustomHUDUtil;
import com.quarri6343.hwaila.api.Accessor;
import com.quarri6343.hwaila.api.BlockAccessorImpl;
import com.quarri6343.hwaila.api.EntityAccessorImpl;

import javax.annotation.Nonnull;

import static com.quarri6343.hwaila.HWaila.HUD_IDENTIFIER;

public class WailaTickSystem extends EntityTickingSystem<EntityStore> {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);
        Player playerComponent = store.getComponent(playerRef, Player.getComponentType());
        assert playerComponent != null;
        PlayerRef playerRefComponent = store.getComponent(playerRef, PlayerRef.getComponentType());
        assert playerRefComponent != null;
        if (!(CustomHUDUtil.getCustomHUD(playerComponent, HUD_IDENTIFIER) instanceof Tooltips tooltips)) {
            return;
        }

        if (!HWaila.getInstance().getConfig().get().isTooltipEnabled(playerRefComponent)) {
            tooltips.update(new UICommandBuilder(), null);
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

        AccessorHolder holder = new AccessorHolder();
        handleEntity(store, commandBuffer, holder, runtime, playerRef);
        if (holder.getAccessor() == null) {
            handleBlock(store, commandBuffer, holder, runtime, playerRef);
        }
        tooltips.update(new UICommandBuilder(), holder.getAccessor());
    }

    //TODO: use BlockAccessorHandler, EntityAccessorHandler to gather information with provider

    private static void handleBlock(Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, AccessorHolder holder, Selector runtime, Ref<EntityStore> playerRef) {
        runtime.selectTargetBlocks(commandBuffer, playerRef, (x, y, z) -> {
            World world = commandBuffer.getExternalData().getWorld();
            ChunkStore chunkStore = world.getChunkStore();
            TransformComponent transformComponent = store.getComponent(playerRef, TransformComponent.getComponentType());
            WorldChunk chunk = chunkStore.getStore()
                    .getComponent(transformComponent.getChunkRef(), WorldChunk.getComponentType());
            BlockType blockType = chunk.getBlockType(x, y, z);
            holder.setAccessor(new BlockAccessorImpl(store, playerRef, blockType));
        });
    }

    private static void handleEntity(Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, AccessorHolder holder, Selector runtime, Ref<EntityStore> playerRef) {
        runtime.selectTargetEntities(commandBuffer, playerRef, ((entityRef, vector4d) -> {
            NPCEntity npcEntity = store.getComponent(entityRef, NPCEntity.getComponentType());
            //TODO: support player entity
            holder.setAccessor(new EntityAccessorImpl(store, playerRef, npcEntity));
        }), _ -> true);
    }

    private static class AccessorHolder {
        private Accessor accessor = null;

        public Accessor getAccessor() {
            return accessor;
        }

        public void setAccessor(Accessor accessor) {
            this.accessor = accessor;
        }
    }
}
