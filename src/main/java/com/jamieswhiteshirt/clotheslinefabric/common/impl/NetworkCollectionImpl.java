package com.jamieswhiteshirt.clotheslinefabric.common.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.jamieswhiteshirt.clotheslinefabric.api.*;
import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Configuration;
import com.jamieswhiteshirt.rtree3i.ConfigurationBuilder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

public final class NetworkCollectionImpl implements NetworkCollection {
    private static final Configuration configuration = new ConfigurationBuilder().star().build();

    private static <E> RTreeMap<Line, E> createEdgesMap() {
        return RTreeMap.create(configuration, Line::getBox);
    }

    private static <N> RTreeMap<BlockPos, N> createNodesMap() {
        return RTreeMap.create(configuration, blockPos -> Box.create(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
            blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1));
    }

    private final List<Network> values = new ArrayList<>();
    private final Int2ObjectMap<Network> byId = new Int2ObjectOpenHashMap<>();
    private final Map<UUID, Network> byUuid = new HashMap<>();
    private RTreeMap<Line, NetworkEdge> edges = createEdgesMap();
    private RTreeMap<BlockPos, NetworkNode> nodes = createNodesMap();
    private final SetMultimap<Long, Network> chunkSpanMap = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    private final Map<Identifier, NetworkCollectionListener> eventListeners = new TreeMap<>();

    @Override
    public List<Network> getValues() {
        return values;
    }

    @Nullable
    @Override
    public Network getById(int id) {
        return byId.get(id);
    }

    @Nullable
    @Override
    public Network getByUuid(UUID uuid) {
        return byUuid.get(uuid);
    }

    @Override
    public void add(Network network) {
        values.add(network);
        byId.put(network.getId(), network);
        byUuid.put(network.getUuid(), network);

        for (Path.Node pathNode : network.getState().getPath().getNodes().values()) {
            nodes = nodes.put(pathNode.getPos(), new NetworkNodeImpl(network, pathNode));
        }
        int i = 0;
        for (Path.Edge pathEdge : network.getState().getPath().getEdges()) {
            edges = edges.put(pathEdge.getLine(), new NetworkEdgeImpl(network, pathEdge, i++));
        }

        for (long position : network.getState().getChunkSpan()) {
            chunkSpanMap.put(position, network);
        }

        for (NetworkCollectionListener eventListener : eventListeners.values()) {
            eventListener.onNetworkAdded(this, network);
        }
    }

    @Override
    public void remove(Network network) {
        values.remove(network);
        byId.remove(network.getId());
        byUuid.remove(network.getUuid());

        for (BlockPos pos : network.getState().getPath().getNodes().keySet()) {
            nodes = nodes.remove(pos);
        }
        for (Path.Edge pathEdge : network.getState().getPath().getEdges()) {
            edges = edges.remove(pathEdge.getLine());
        }

        for (long position : network.getState().getChunkSpan()) {
            chunkSpanMap.remove(position, network);
        }

        for (NetworkCollectionListener eventListener : eventListeners.values()) {
            eventListener.onNetworkRemoved(this, network);
        }
    }

    @Override
    public void removeById(int id) {
        Network network = getById(id);
        if (network != null) remove(network);
    }

    @Override
    public void removeByUuid(UUID uuid) {
        Network network = getByUuid(uuid);
        if (network != null) remove(network);
    }

    @Override
    public RTreeMap<BlockPos, NetworkNode> getNodes() {
        return nodes;
    }

    @Override
    public RTreeMap<Line, NetworkEdge> getEdges() {
        return edges;
    }

    @Override
    public Set<Network> getNetworksSpanningChunk(long pos) {
        return chunkSpanMap.get(pos);
    }

    @Override
    public void addEventListener(Identifier key, NetworkCollectionListener eventListener) {
        eventListeners.put(key, eventListener);
    }

    @Override
    public void removeEventListener(Identifier key) {
        eventListeners.remove(key);
    }
}
