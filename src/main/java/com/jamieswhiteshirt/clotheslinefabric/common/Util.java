package com.jamieswhiteshirt.clotheslinefabric.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class Util {
    /**
     * Forge really likes annotation magic. This makes static analysis tools shut up.
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> T nonNullInjected() {
        return null;
    }

    public static boolean isCreativePlayer(@Nullable Entity entity) {
        return entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative();
    }
}
