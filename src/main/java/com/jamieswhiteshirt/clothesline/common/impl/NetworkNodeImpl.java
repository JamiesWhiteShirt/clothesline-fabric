package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.NetworkNode;
import com.jamieswhiteshirt.clothesline.api.Path;

import java.util.Objects;

public final class NetworkNodeImpl implements NetworkNode {
    private final Network network;
    private final Path.Node pathNode;

    public NetworkNodeImpl(Network network, Path.Node pathNode) {
        this.network = network;
        this.pathNode = pathNode;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public Path.Node getPathNode() {
        return pathNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkNodeImpl node = (NetworkNodeImpl) o;
        return Objects.equals(network, node.network) &&
            Objects.equals(pathNode, node.pathNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, pathNode);
    }
}
