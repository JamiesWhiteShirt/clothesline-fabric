package com.jamieswhiteshirt.clotheslinefabric.internal;

import net.minecraft.world.chunk.ChunkPos;

public interface NetworkCollectionTracker<T> {
    void onWatchChunk(T watcher, ChunkPos pos);

    void onUnWatchChunk(T watcher, ChunkPos pos);

    void update();
}
