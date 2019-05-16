package com.jamieswhiteshirt.clotheslinefabric.common.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkCollection;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkCollectionListener;
import com.jamieswhiteshirt.clotheslinefabric.internal.NetworkCollectionTracker;
import com.jamieswhiteshirt.clotheslinefabric.internal.NetworkMessenger;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

import java.util.Collection;
import java.util.function.Function;

public final class NetworkCollectionTrackerImpl<T> implements NetworkCollectionTracker<T> {
    private static final Identifier LISTENER_KEY = new Identifier("clothesline", "watcher");

    private final NetworkCollection networks;
    private final Function<ChunkPos, Collection<T>> getChunkWatchers;
    private final NetworkMessenger<T> messenger;
    private final Int2ObjectMap<NetworkTracker<T>> networkTrackers = new Int2ObjectOpenHashMap<>();

    public NetworkCollectionTrackerImpl(NetworkCollection networks, Function<ChunkPos, Collection<T>> getChunkWatchers, NetworkMessenger<T> messenger) {
        this.networks = networks;
        this.getChunkWatchers = getChunkWatchers;
        this.messenger = messenger;

        networks.addEventListener(LISTENER_KEY, new NetworkCollectionListener() {
            @Override
            public void onNetworkAdded(NetworkCollection networks, Network network) {
                addNetworkWatcher(network);
            }

            @Override
            public void onNetworkRemoved(NetworkCollection networks, Network network) {
                removeNetworkWatcher(network);
            }
        });
    }

    @Override
    public void onWatchChunk(T watcher, ChunkPos pos) {
        for (Network network : networks.getNetworksSpanningChunk(pos.toLong())) {
            networkTrackers.get(network.getId()).addWatcher(watcher);
        }
    }

    @Override
    public void onUnWatchChunk(T watcher, ChunkPos pos) {
        for (Network network : networks.getNetworksSpanningChunk(pos.toLong())) {
            networkTrackers.get(network.getId()).removeWatcher(watcher);
        }
    }

    @Override
    public void update() {
        for (NetworkTracker<T> tracker : networkTrackers.values()) {
            tracker.update();
        }
    }

    private void addNetworkWatcher(Network network) {
        NetworkTracker<T> networkTracker = new NetworkTracker<>(network, messenger);
        network.addEventListener(LISTENER_KEY, networkTracker);
        networkTrackers.put(network.getId(), networkTracker);

        // Players may already be watching chunks that the network intersects with
        for (long position : network.getState().getChunkSpan()) {
            for (T watcher : getChunkWatchers.apply(new ChunkPos(position))) {
                networkTracker.addWatcher(watcher);
            }
        }
    }

    private void removeNetworkWatcher(Network network) {
        network.removeEventListener(LISTENER_KEY);
        networkTrackers.remove(network.getId()).clear();
    }
}
