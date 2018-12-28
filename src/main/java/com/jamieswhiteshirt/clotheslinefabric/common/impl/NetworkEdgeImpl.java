package com.jamieswhiteshirt.clotheslinefabric.common.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.Path;
import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkEdge;

import java.util.Objects;

public class NetworkEdgeImpl implements NetworkEdge {
    private final Network network;
    private final Path.Edge pathEdge;
    private final int index;

    public NetworkEdgeImpl(Network network, Path.Edge pathEdge, int index) {
        this.network = network;
        this.pathEdge = pathEdge;
        this.index = index;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public Path.Edge getPathEdge() {
        return pathEdge;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkEdgeImpl edge = (NetworkEdgeImpl) o;
        return index == edge.index &&
            Objects.equals(pathEdge, edge.pathEdge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, index);
    }
}
