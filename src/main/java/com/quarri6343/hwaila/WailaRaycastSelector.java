package com.quarri6343.hwaila;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.RaycastSelector;

public class WailaRaycastSelector extends RaycastSelector {

    public void setOffset(Vector3d vector3d) {
        this.offset = vector3d;
    }
}
