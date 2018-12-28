package com.jamieswhiteshirt.clotheslinefabric.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkCollection;
import com.jamieswhiteshirt.clotheslinefabric.internal.PersistentNetwork;
import com.jamieswhiteshirt.clotheslinefabric.common.impl.NetworkCollectionImpl;
import com.jamieswhiteshirt.clotheslinefabric.common.impl.NetworkProviderImpl;
import com.jamieswhiteshirt.clotheslinefabric.common.util.NetworkStateBuilder;
import com.jamieswhiteshirt.clotheslinefabric.internal.NetworkProvider;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class NetworkProviderTest {
    PersistentNetwork network0 = createPersistentNetwork(new UUID(0, 0), new BlockPos(0, 0, 0), new BlockPos(16, 0, 0));
    ChunkPos chunk0 = new ChunkPos(0, 0);
    ChunkPos chunk1 = new ChunkPos(0, 1);

    NetworkProvider provider;
    NetworkCollection collection;
    LongSet loadedChunks;


    @BeforeEach
    void resetCollection() {
        collection = new NetworkCollectionImpl();
        loadedChunks = new LongOpenHashSet();
        provider = new NetworkProviderImpl(collection, pos -> loadedChunks.contains(pos));
    }

    PersistentNetwork createPersistentNetwork(UUID uuid, BlockPos pos0, BlockPos pos1) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos0);
        stateBuilder.addEdge(pos0, pos1);
        return new PersistentNetwork(uuid, stateBuilder.build());
    }

    void loadChunk(ChunkPos pos) {
        loadedChunks.add(pos.toLong());
        provider.onChunkLoaded(pos);
    }

    void unloadChunk(ChunkPos pos) {
        loadedChunks.remove(pos.toLong());
        provider.onChunkUnloaded(pos);
    }

    @Test
    void loadsNetworkForFirstLoadedChunk() {
        provider.addNetwork(network0);

        Assertions.assertNull(collection.getByUuid(network0.getUuid()));

        loadChunk(chunk0);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void loadsNetworkForExistingLoadedChunk() {
        loadChunk(chunk0);
        provider.addNetwork(network0);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void unloadsNetworkForLastUnloadedChunk() {
        loadChunk(chunk0);
        loadChunk(chunk1);
        provider.addNetwork(network0);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));

        unloadChunk(chunk1);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));

        unloadChunk(chunk0);

        Assertions.assertNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void unloadsNetworkWhenRemoved() {
        loadChunk(chunk0);
        provider.addNetwork(network0);

        Assertions.assertNotNull(collection.getByUuid(network0.getUuid()));

        provider.removeNetwork(network0.getUuid());

        Assertions.assertNull(collection.getByUuid(network0.getUuid()));
    }
}
