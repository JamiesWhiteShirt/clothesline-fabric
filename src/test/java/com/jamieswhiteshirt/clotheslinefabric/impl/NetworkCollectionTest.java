package com.jamieswhiteshirt.clotheslinefabric.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.*;
import com.jamieswhiteshirt.clotheslinefabric.common.impl.NetworkImpl;
import com.jamieswhiteshirt.clotheslinefabric.common.impl.NetworkCollectionImpl;
import com.jamieswhiteshirt.clotheslinefabric.common.util.NetworkStateBuilder;
import com.jamieswhiteshirt.clotheslinefabric.internal.PersistentNetwork;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class NetworkCollectionTest {
    Identifier eventListenerKey = new Identifier("test", "test");

    NetworkCollection collection;
    Network network0 = createNetwork(0, new UUID(0, 0), new BlockPos(0, 0, 0), new BlockPos(1, 1, 1));

    @BeforeEach
    void resetCollection() {
        collection = new NetworkCollectionImpl();
    }

    Network createNetwork(int id, UUID uuid, BlockPos pos0, BlockPos pos1) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos0);
        stateBuilder.addEdge(pos0, pos1);
        return new NetworkImpl(id, new PersistentNetwork(uuid, stateBuilder.build()));
    }

    @Test
    void adds() {
        collection.add(network0);
        Assertions.assertTrue(collection.getValues().contains(network0));
    }

    @Test
    void getsById() {
        collection.add(network0);
        Assertions.assertEquals(network0, collection.getById(network0.getId()));
    }

    @Test
    void getsByUuid() {
        collection.add(network0);
        Assertions.assertEquals(network0, collection.getByUuid(network0.getUuid()));
    }

    @Test
    void removesByValue() {
        collection.add(network0);
        collection.remove(network0);
        Assertions.assertFalse(collection.getValues().contains(network0));
        Assertions.assertNull(collection.getById(network0.getId()));
        Assertions.assertNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void removesById() {
        collection.add(network0);
        collection.removeById(network0.getId());
        Assertions.assertFalse(collection.getValues().contains(network0));
        Assertions.assertNull(collection.getById(network0.getId()));
        Assertions.assertNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void removesByUuid() {
        collection.add(network0);
        collection.removeByUuid(network0.getUuid());
        Assertions.assertFalse(collection.getValues().contains(network0));
        Assertions.assertNull(collection.getById(network0.getId()));
        Assertions.assertNull(collection.getByUuid(network0.getUuid()));
    }

    @Test
    void maintainsOrderOfInsertion() {
        Network network1 = createNetwork(1, new UUID(1, 1), new BlockPos(1, 0, 0), new BlockPos(2, 1, 1));
        Network network2 = createNetwork(2, new UUID(2, 2), new BlockPos(2, 0, 0), new BlockPos(3, 1, 1));

        collection.add(network0);
        collection.add(network1);
        collection.add(network2);

        Assertions.assertEquals(Arrays.asList(network0, network1, network2), collection.getValues());

        collection.remove(network1);

        Assertions.assertEquals(Arrays.asList(network0, network2), collection.getValues());

        collection.add(network1);

        Assertions.assertEquals(Arrays.asList(network0, network2, network1), collection.getValues());
    }

    @Test
    void indexesPathNodes() {
        collection.add(network0);

        for (Map.Entry<BlockPos, Path.Node> entry : network0.getState().getPath().getNodes().entrySet()) {
            NetworkNode networkNode = collection.getNodes().get(entry.getKey());

            Assertions.assertEquals(network0, networkNode.getNetwork());
            Assertions.assertEquals(entry.getValue(), networkNode.getPathNode());
        }

        collection.remove(network0);

        for (BlockPos pos : network0.getState().getPath().getNodes().keySet()) {
            Assertions.assertNull(collection.getNodes().get(pos));
        }
    }

    @Test
    void indexesPathEdges() {
        collection.add(network0);

        int i = 0;
        for (Path.Edge pathEdge : network0.getState().getPath().getEdges()) {
            NetworkEdge networkEdge = collection.getEdges().get(pathEdge.getLine());
            Assertions.assertEquals(network0, networkEdge.getNetwork());
            Assertions.assertEquals(pathEdge, networkEdge.getPathEdge());
            Assertions.assertEquals(i++, networkEdge.getIndex());
        }

        collection.remove(network0);

        for (Path.Edge pathEdge : network0.getState().getPath().getEdges()) {
            Assertions.assertNull(collection.getEdges().get(pathEdge.getLine()));
        }
    }

    @Test
    void indexesChunkSpan() {
        collection.add(network0);

        for (long position : network0.getState().getChunkSpan()) {
            Assertions.assertEquals(Collections.singleton(network0), collection.getNetworksSpanningChunk(position));
        }

        collection.remove(network0);

        for (long position : network0.getState().getChunkSpan()) {
            Assertions.assertEquals(Collections.emptySet(), collection.getNetworksSpanningChunk(position));
        }
    }

    @Test
    void addFiresEvents() {
        NetworkCollectionListener listener = Mockito.mock(NetworkCollectionListener.class);
        collection.addEventListener(eventListenerKey, listener);

        collection.add(network0);

        Mockito.verify(listener).onNetworkAdded(collection, network0);
    }

    @Test
    void removeFiresEvents() {
        collection.add(network0);

        NetworkCollectionListener listener = Mockito.mock(NetworkCollectionListener.class);
        collection.addEventListener(eventListenerKey, listener);

        collection.remove(network0);

        Mockito.verify(listener).onNetworkRemoved(collection, network0);
    }
}
