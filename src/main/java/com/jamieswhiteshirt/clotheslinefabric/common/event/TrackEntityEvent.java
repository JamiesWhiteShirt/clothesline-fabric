package com.jamieswhiteshirt.clotheslinefabric.common.event;

import net.fabricmc.fabric.util.HandlerArray;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.BiConsumer;

public final class TrackEntityEvent {
    public static final HandlerRegistry<BiConsumer<ServerPlayerEntity, Entity>> START = new HandlerArray<>(BiConsumer.class);

    private TrackEntityEvent() { }
}
