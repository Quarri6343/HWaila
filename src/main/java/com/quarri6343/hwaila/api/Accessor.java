package com.quarri6343.hwaila.api;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public interface Accessor {

    @Nullable
    Object getTarget();

    Store<EntityStore> getStore();

    Ref<EntityStore> getPlayerRef();
}
