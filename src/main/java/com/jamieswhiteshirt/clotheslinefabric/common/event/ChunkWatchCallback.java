package com.jamieswhiteshirt.clotheslinefabric.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface ChunkWatchCallback {
    Event<ChunkWatchCallback> WATCH = EventFactory.createArrayBacked(ChunkWatchCallback.class, (listeners) -> (world, pos, playerEntity) -> {
        for (ChunkWatchCallback callback : listeners) {
            callback.accept(world, pos, playerEntity);
        }
    });
    Event<ChunkWatchCallback> UNWATCH = EventFactory.createArrayBacked(ChunkWatchCallback.class, (listeners) -> (world, pos, playerEntity) -> {
        for (ChunkWatchCallback callback : listeners) {
            callback.accept(world, pos, playerEntity);
        }
    });

    void accept(World world, ChunkPos pos, ServerPlayerEntity playerEntity);
}
