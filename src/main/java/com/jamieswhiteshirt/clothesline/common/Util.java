package com.jamieswhiteshirt.clothesline.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import org.jetbrains.annotations.Nullable;

public class Util {
    public static boolean isCreativePlayer(@Nullable Entity entity) {
        return entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative();
    }
}
