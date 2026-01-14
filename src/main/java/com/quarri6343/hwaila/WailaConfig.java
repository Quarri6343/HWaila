package com.quarri6343.hwaila;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WailaConfig {
    public Set<UUID> tooltipBlackList = new HashSet<>();
    public static final BuilderCodec CODEC;

    static {
        CODEC =
                (
                    BuilderCodec
                            .builder(WailaConfig.class, WailaConfig::new)
                            .append(
                                    new KeyedCodec(
                                            "BlackList",
                                            new ArrayCodec<>(Codec.UUID_STRING, UUID[]::new)
                                    ),
                                    (cfg, arr) -> {
                                        cfg.tooltipBlackList.clear();
                                        cfg.tooltipBlackList.addAll(Arrays.asList((UUID[]) arr));
                                    },
                                    (cfg) -> cfg.tooltipBlackList.toArray(new UUID[0])
                            )
                            .add()
                ).build();
    }
}
