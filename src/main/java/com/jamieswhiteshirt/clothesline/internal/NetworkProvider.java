package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.util.math.ChunkPos;

import java.util.Collection;
import java.util.UUID;

public interface NetworkProvider {
    void reset(Collection<PersistentNetwork> persistentNetworks);

    Collection<PersistentNetwork> getNetworks();

    void addNetwork(PersistentNetwork persistentNetwork);

    void removeNetwork(UUID uuid);

    void onChunkLoaded(ChunkPos pos);

    void onChunkUnloaded(ChunkPos pos);
}
