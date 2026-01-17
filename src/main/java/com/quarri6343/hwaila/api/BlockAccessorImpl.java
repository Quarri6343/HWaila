package com.quarri6343.hwaila.api;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class BlockAccessorImpl extends AccessorImpl implements BlockAccessor {

    private final BlockType blockType;

    public BlockAccessorImpl(Store<EntityStore> store, Ref<EntityStore> playerRef, BlockType blockType) {
        super(store, playerRef);
        this.blockType = blockType;
    }

    @Override
    public BlockType getBlockType() {
        return blockType;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return blockType;
    }
}
