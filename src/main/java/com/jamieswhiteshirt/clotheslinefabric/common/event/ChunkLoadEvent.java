package com.jamieswhiteshirt.clotheslinefabric.common.event;

import net.fabricmc.fabric.util.HandlerArray;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;

import java.util.function.BiConsumer;

public final class ChunkLoadEvent {
    public static final HandlerRegistry<BiConsumer<World, ChunkPos>> LOAD = new HandlerArray<>(BiConsumer.class);
    public static final HandlerRegistry<BiConsumer<World, ChunkPos>> UNLOAD = new HandlerArray<>(BiConsumer.class);

    private ChunkLoadEvent() { }
}
