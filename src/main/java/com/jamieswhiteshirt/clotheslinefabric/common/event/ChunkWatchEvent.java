package com.jamieswhiteshirt.clotheslinefabric.common.event;

import net.fabricmc.fabric.util.HandlerArray;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;

public final class ChunkWatchEvent {
    @FunctionalInterface
    public interface ChunkWatchEventConsumer {
        void accept(World world, ChunkPos pos, ServerPlayerEntity playerEntity);
    }

    public static final HandlerRegistry<ChunkWatchEventConsumer> WATCH = new HandlerArray<>(ChunkWatchEventConsumer.class);
    public static final HandlerRegistry<ChunkWatchEventConsumer> UNWATCH = new HandlerArray<>(ChunkWatchEventConsumer.class);

    private ChunkWatchEvent() { }
}
