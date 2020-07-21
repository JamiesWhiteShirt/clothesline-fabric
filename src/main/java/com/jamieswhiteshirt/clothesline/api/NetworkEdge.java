package com.jamieswhiteshirt.clothesline.api;

public interface NetworkEdge {
    Network getNetwork();

    Path.Edge getPathEdge();

    int getIndex();
}
