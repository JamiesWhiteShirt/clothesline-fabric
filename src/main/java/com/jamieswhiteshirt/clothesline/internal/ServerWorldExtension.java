package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

public interface ServerWorldExtension extends WorldExtension {
    void clothesline$onPlayerWatchChunk(ChunkPos pos, ServerPlayerEntity player);

    void clothesline$onPlayerUnWatchChunk(ChunkPos pos, ServerPlayerEntity player);

    void clothesline$onChunkLoaded(ChunkPos pos);

    void clothesline$onChunkUnloaded(ChunkPos pos);
}
