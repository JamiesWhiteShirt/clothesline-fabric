package com.jamieswhiteshirt.clotheslinefabric.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;

@FunctionalInterface
public interface ChunkLoadCallback {
    Event<ChunkLoadCallback> LOAD = EventFactory.createArrayBacked(ChunkLoadCallback.class, (listeners) -> (world, chunkPos) -> {
        for (ChunkLoadCallback callback : listeners) {
            callback.accept(world, chunkPos);
        }
    });
    Event<ChunkLoadCallback> UNLOAD = EventFactory.createArrayBacked(ChunkLoadCallback.class, (listeners) -> (world, chunkPos) -> {
        for (ChunkLoadCallback callback : listeners) {
            callback.accept(world, chunkPos);
        }
    });

    void accept(World world, ChunkPos chunkPos);
}
