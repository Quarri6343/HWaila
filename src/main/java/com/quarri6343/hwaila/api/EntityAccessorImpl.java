package com.quarri6343.hwaila.api;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class EntityAccessorImpl extends AccessorImpl implements EntityAccessor {

    private final Entity entity;

    public EntityAccessorImpl(Store<EntityStore> store, Ref<EntityStore> playerRef, Entity entity) {
        super(store, playerRef);
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return entity;
    }
}
