package com.jamieswhiteshirt.clotheslinefabric.api;

public interface NetworkCollectionListener {
    void onNetworkAdded(NetworkCollection networkMap, Network network);

    void onNetworkRemoved(NetworkCollection networkMap, Network network);
}
