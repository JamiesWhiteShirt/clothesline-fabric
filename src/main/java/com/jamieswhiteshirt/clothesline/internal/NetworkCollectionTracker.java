package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.util.math.ChunkPos;

public interface NetworkCollectionTracker<T> {
    void onWatchChunk(T watcher, ChunkPos pos);

    void onUnWatchChunk(T watcher, ChunkPos pos);

    void update();
}
