package com.jamieswhiteshirt.clotheslinefabric.api;

import com.jamieswhiteshirt.rtree3i.RTreeMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NetworkCollection {
    /**
     * Returns the collection of clothesline networks.
     * @return the collection of clothesline networks
     */
    List<Network> getValues();

    /**
     * Get a clothesline network by its shorter ID, or null if no clothesline network with the ID exists.
     * @see Network#getId()
     * @param id the shorter ID
     * @return a clothesline network, or null if no clothesline network with the ID exists
     */
    @Nullable
    Network getById(int id);

    /**
     * Get a clothesline network by its longer UUID, or null if no clothesline network with the UUID exists.
     * @see Network#getUuid()
     * @param uuid the longer UUID
     * @return a clothesline network, or null if no clothesline network with the UUID exists
     */
    @Nullable
    Network getByUuid(UUID uuid);

    /**
     * Adds the clothesline network to the collection of clothesline networks.
     *
     * Notifies event listeners with
     * {@link NetworkCollectionListener#onNetworkRemoved(NetworkCollection, Network)}.
     * @param network the clothesline network
     */
    void add(Network network);

    /**
     * Removes the clothesline network from the collection of clothesline networks.
     *
     * Notifies event listeners with
     * {@link NetworkCollectionListener#onNetworkRemoved(NetworkCollection, Network)}.
     * @param network the clothesline network
     */
    void remove(Network network);

    /**
     * Removes the clothesline network by its shorter ID from the collection of clothesline networks.
     *
     * Notifies event listeners with
     * {@link NetworkCollectionListener#onNetworkRemoved(NetworkCollection, Network)}.
     * @param id the shorter ID
     */
    void removeById(int id);

    /**
     * Removes the clothesline network by its longer UUID from the collection of clothesline networks.
     *
     * Notifies event listeners with
     * {@link NetworkCollectionListener#onNetworkRemoved(NetworkCollection, Network)}.
     * @param uuid the longer UUID
     */
    void removeByUuid(UUID uuid);

    /**
     * Returns a spatial index of all the path edges of all currently loaded networks.
     * @return a spatial index of all the path edges of all currently loaded networks
     */
    RTreeMap<Line, NetworkEdge> getEdges();

    /**
     * Returns a spatial index of all the path nodes of all currently loaded networks.
     * @return a spatial index of all the path nodes of all currently loaded networks
     */
    RTreeMap<BlockPos, NetworkNode> getNodes();

    /**
     * Returns a set of networks spanning the chunk at the specified position. This set must not be modified
     * @return a set of networks spanning the chunk at the specified position
     */
    Set<Network> getNetworksSpanningChunk(long pos);

    /**
     * Adds an event listener that will be notified when the collection of clothesline networks changes. The event
     * listener is bound by a key which must be unique for the clothesline network. If an existing event listener is
     * bound to the same key, it will be overridden.
     *
     * The event listener can be removed with {@link #removeEventListener(Identifier)}.
     * @param key the event listener key
     * @param eventListener the event listener
     */
    void addEventListener(Identifier key, NetworkCollectionListener eventListener);

    /**
     * Removes an event listener bound to the specified key with
     * {@link #addEventListener(Identifier, NetworkCollectionListener)}. If no event listener is bound to the
     * key, nothing happens.
     * @param key the event listener key
     */
    void removeEventListener(Identifier key);
}
