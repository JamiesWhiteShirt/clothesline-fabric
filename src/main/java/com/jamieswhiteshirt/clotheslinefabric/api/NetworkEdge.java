package com.jamieswhiteshirt.clotheslinefabric.api;

public interface NetworkEdge {
    Network getNetwork();

    Path.Edge getPathEdge();

    int getIndex();
}
