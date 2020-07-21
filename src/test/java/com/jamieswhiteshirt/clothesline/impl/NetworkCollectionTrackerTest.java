package com.jamieswhiteshirt.clothesline.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.NetworkCollection;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollectionTrackerImpl;
import com.jamieswhiteshirt.clothesline.internal.PersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkImpl;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollectionImpl;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import com.jamieswhiteshirt.clothesline.internal.NetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.internal.NetworkMessenger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public class NetworkCollectionTrackerTest {
    Network network0;
    NetworkCollection collection;
    NetworkMessenger<Object> messenger;
    SetMultimap<Long, Object> chunkWatchers;
    NetworkCollectionTracker<Object> tracker;
    Object watcher;

    long chunk0 = ChunkPos.toLong(0, 0);
    long chunk1 = ChunkPos.toLong(1, 0);

    Network createNetwork(int id, UUID uuid, BlockPos pos0, BlockPos pos1) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos0);
        stateBuilder.addEdge(pos0, pos1);
        return new NetworkImpl(id, new PersistentNetwork(uuid, stateBuilder.build()));
    }

    void watchChunk(long position, Object watcher) {
        chunkWatchers.put(position, watcher);
        tracker.onWatchChunk(watcher, new ChunkPos(position));
    }

    void unWatchChunk(long position, Object watcher) {
        chunkWatchers.remove(position, watcher);
        tracker.onUnWatchChunk(watcher, new ChunkPos(position));
    }

    @BeforeEach
    void resetTracker() {
        network0 = createNetwork(0, new UUID(0, 0), new BlockPos(0, 0, 0), new BlockPos(16, 0, 0));
        collection = new NetworkCollectionImpl();
        messenger = Mockito.mock(NetworkMessenger.class);
        chunkWatchers = HashMultimap.create();
        Function<ChunkPos, Collection<Object>> getChunkWatchers = (ChunkPos pos) -> chunkWatchers.get(pos.toLong());
        tracker = new NetworkCollectionTrackerImpl<>(collection, getChunkWatchers, messenger);
        watcher = new Object();
    }

    @Test
    void sendsAddNetworkForFirstWatchedChunk() {
        collection.add(network0);

        Mockito.verifyNoMoreInteractions(messenger);

        watchChunk(chunk0, watcher);

        Mockito.verify(messenger).addNetwork(watcher, network0);

        watchChunk(chunk1, watcher);

        Mockito.verifyNoMoreInteractions(messenger);
    }

    @Test
    void sendsAddNetworkForExistingWatchedChunk() {
        watchChunk(chunk0, watcher);
        watchChunk(chunk1, watcher);

        Mockito.verifyNoMoreInteractions(messenger);

        collection.add(network0);

        Mockito.verify(messenger).addNetwork(watcher, network0);
        Mockito.verifyNoMoreInteractions(messenger);
    }

    @Test
    void sendsRemoveNetworkForLastUnWatchedChunk() {
        collection.add(network0);
        watchChunk(chunk0, watcher);

        Mockito.verify(messenger).addNetwork(watcher, network0);

        watchChunk(chunk1, watcher);
        unWatchChunk(chunk1, watcher);

        Mockito.verifyNoMoreInteractions(messenger);

        unWatchChunk(chunk0, watcher);

        Mockito.verify(messenger).removeNetwork(watcher, network0);
        Mockito.verifyNoMoreInteractions(messenger);
    }

    @Test
    void sendsRemoveNetworkForRemovedNetwork() {
        watchChunk(chunk0, watcher);
        collection.add(network0);

        Mockito.verify(messenger).addNetwork(watcher, network0);

        collection.remove(network0);

        Mockito.verify(messenger).removeNetwork(watcher, network0);
        Mockito.verifyNoMoreInteractions(messenger);
    }

    @Test
    void sendsUpdateMessagesForTrackedNetwork() {
        watchChunk(chunk0, watcher);
        collection.add(network0);

        Mockito.verify(messenger).addNetwork(watcher, network0);

        network0.getState().setShift(1);
        tracker.update();

        Mockito.verify(messenger).setShiftAndMomentum(watcher, network0, 1, 0);

        network0.getState().setMomentum(1);
        tracker.update();

        Mockito.verify(messenger).setShiftAndMomentum(watcher, network0, 1, 1);
    }
}
