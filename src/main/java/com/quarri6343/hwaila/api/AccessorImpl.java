package com.quarri6343.hwaila.api;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public abstract class AccessorImpl implements Accessor {

    private final Store<EntityStore> store;
    private final Ref<EntityStore> playerRef;

    protected AccessorImpl(Store<EntityStore> store, Ref<EntityStore> playerRef) {
        this.store = store;
        this.playerRef = playerRef;
    }

    @Override
    public Store<EntityStore> getStore() {
        return store;
    }

    @Override
    public Ref<EntityStore> getPlayerRef() {
        return playerRef;
    }
}
