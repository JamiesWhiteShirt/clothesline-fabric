package com.jamieswhiteshirt.clotheslinefabric.common.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkCollection;
import com.jamieswhiteshirt.clotheslinefabric.internal.PersistentNetwork;
import com.jamieswhiteshirt.clotheslinefabric.internal.NetworkProvider;
import net.minecraft.world.chunk.ChunkPos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;

public final class NetworkProviderImpl implements NetworkProvider {
    private final NetworkCollection networks;
    private final LongPredicate isChunkLoaded;
    private Map<UUID, NetworkProviderEntry> entryMap = new HashMap<>();
    private SetMultimap<Long, UUID> chunkMap = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    private int nextNetworkId = 0;

    public NetworkProviderImpl(NetworkCollection networks, LongPredicate isChunkLoaded) {
        this.networks = networks;
        this.isChunkLoaded = isChunkLoaded;
    }

    private void chunkLoaded(NetworkProviderEntry entry) {
        if (entry.incrementLoadCount()) {
            NetworkImpl network = new NetworkImpl(nextNetworkId++, entry.getPersistentNetwork());
            networks.add(network);
        }
    }

    @Override
    public void reset(Collection<PersistentNetwork> persistentNetworks) {
        for (UUID uuid : entryMap.keySet()) {
            Network network = networks.getByUuid(uuid);
            if (network != null) networks.remove(network);
        }

        nextNetworkId = 0;
        entryMap = new HashMap<>();
        chunkMap = MultimapBuilder.hashKeys().linkedHashSetValues().build();
        for (PersistentNetwork persistentNetwork : persistentNetworks) {
            addNetwork(persistentNetwork);
        }
    }

    @Override
    public Collection<PersistentNetwork> getNetworks() {
        return entryMap.values().stream().map(NetworkProviderEntry::getPersistentNetwork).collect(Collectors.toList());
    }

    @Override
    public void addNetwork(PersistentNetwork persistentNetwork) {
        NetworkProviderEntry entry = new NetworkProviderEntry(persistentNetwork);
        entryMap.put(persistentNetwork.getUuid(), entry);
        for (long position : entry.getPersistentNetwork().getState().getChunkSpan()) {
            chunkMap.put(position, persistentNetwork.getUuid());
            // Increment load count if this network spans an already loaded chunk
            if (isChunkLoaded.test(position)) {
                chunkLoaded(entry);
            }
        }
    }

    @Override
    public void removeNetwork(UUID uuid) {
        NetworkProviderEntry entry = entryMap.remove(uuid);
        for (long position : entry.getPersistentNetwork().getState().getChunkSpan()) {
            chunkMap.remove(position, uuid);
        }

        // Remove network if it is loaded
        networks.removeByUuid(uuid);
    }

    @Override
    public void onChunkLoaded(ChunkPos pos) {
        for (UUID uuid : chunkMap.get(pos.toLong())) {
            NetworkProviderEntry entry = entryMap.get(uuid);
            chunkLoaded(entry);
        }
    }

    @Override
    public void onChunkUnloaded(ChunkPos pos) {
        for (UUID uuid : chunkMap.get(pos.toLong())) {
            NetworkProviderEntry entry = entryMap.get(uuid);
            if (entry.decrementLoadCount()) {
                networks.removeByUuid(uuid);
            }
        }
    }
}
