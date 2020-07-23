package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

public interface ServerWorldExtension extends WorldExtension {
    void onPlayerWatchChunk(ChunkPos pos, ServerPlayerEntity player);

    void onPlayerUnWatchChunk(ChunkPos pos, ServerPlayerEntity player);

    void onChunkLoaded(ChunkPos pos);

    void onChunkUnloaded(ChunkPos pos);
}
