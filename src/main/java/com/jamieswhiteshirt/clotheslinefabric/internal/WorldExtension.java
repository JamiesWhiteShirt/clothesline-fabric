package com.jamieswhiteshirt.clotheslinefabric.internal;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.ChunkPos;

public interface WorldExtension {
    void clotheslineTick();

    void onPlayerWatchChunk(ChunkPos pos, ServerPlayerEntity player);

    void onPlayerUnWatchChunk(ChunkPos pos, ServerPlayerEntity player);

    void onChunkLoaded(ChunkPos pos);

    void onChunkUnloaded(ChunkPos pos);
}
