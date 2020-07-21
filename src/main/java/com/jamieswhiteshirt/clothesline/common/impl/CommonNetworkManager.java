package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class CommonNetworkManager implements NetworkManager {
    private final World world;
    private final NetworkCollection networks;

    protected CommonNetworkManager(World world, NetworkCollection networks) {
        this.world = world;
        this.networks = networks;
    }

    protected abstract void createNetwork(NetworkState networkState);

    protected abstract void deleteNetwork(Network network);

    protected abstract void dropItems(NetworkState state, boolean dropClotheslines);

    @Override
    public NetworkCollection getNetworks() {
        return networks;
    }

    @Override
    public final void update() {
        world.getProfiler().push("tickClotheslines");
        networks.getValues().forEach(Network::update);
        world.getProfiler().pop();
    }

    private void extend(Network network, BlockPos fromPos, BlockPos toPos) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.fromAbsolute(network.getState());
        stateBuilder.addEdge(fromPos, toPos);

        deleteNetwork(network);
        createNetwork(stateBuilder.build());
    }

    @Override
    public final boolean connect(BlockPos fromPos, BlockPos toPos) {
        if (fromPos.equals(toPos)) {
            NetworkNode node = networks.getNodes().get(fromPos);
            if (node != null) {
                Network network = node.getNetwork();
                NetworkStateBuilder stateBuilder = NetworkStateBuilder.fromAbsolute(network.getState());
                stateBuilder.reroot(toPos);

                deleteNetwork(network);
                createNetwork(stateBuilder.build());
            }
            return false;
        }

        NetworkNode fromNode = networks.getNodes().get(fromPos);
        NetworkNode toNode = networks.getNodes().get(toPos);

        if (fromNode != null) {
            Network fromNetwork = fromNode.getNetwork();
            if (toNode != null) {
                Network toNetwork = toNode.getNetwork();

                if (fromNetwork == toNetwork) {
                    //TODO: Look into circular networks
                    return false;
                }

                deleteNetwork(fromNetwork);
                deleteNetwork(toNetwork);

                NetworkStateBuilder fromState = NetworkStateBuilder.fromAbsolute(fromNetwork.getState());
                NetworkStateBuilder toState = NetworkStateBuilder.fromAbsolute(toNetwork.getState());
                toState.reroot(toPos);
                fromState.addSubState(fromPos, toState);

                createNetwork(fromState.build());
            } else {
                extend(fromNetwork, fromPos, toPos);
            }
        } else {
            if (toNode != null) {
                Network toNetwork = toNode.getNetwork();
                extend(toNetwork, toPos, fromPos);
            } else {
                NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, fromPos);
                stateBuilder.addEdge(fromPos, toPos);
                createNetwork(stateBuilder.build());
            }
        }

        return true;
    }

    private void applySplitResult(NetworkStateBuilder.SplitResult splitResult, boolean dropClotheslines) {
        for (NetworkStateBuilder subState : splitResult.getSubStates()) {
            createNetwork(subState.build());
        }
        dropItems(splitResult.getState().build(), dropClotheslines);
    }


    private boolean removeConnection(BlockPos posA, BlockPos posB, boolean dropItems) {
        if (posA.equals(posB)) {
            return false;
        }

        NetworkNode nodeA = networks.getNodes().get(posA);
        NetworkNode nodeB = networks.getNodes().get(posB);
        if (nodeA != null && nodeB != null) {
            Network network = nodeA.getNetwork();
            if (network == nodeB.getNetwork()) {
                NetworkStateBuilder state = NetworkStateBuilder.fromAbsolute(network.getState());
                state.reroot(posA);
                deleteNetwork(network);
                applySplitResult(state.splitEdge(posB), dropItems);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean breakConnection(@Nullable LivingEntity entity, BlockPos posA, BlockPos posB) {
        return removeConnection(posA, posB, !Util.isCreativePlayer(entity));
    }

    @Override
    public boolean removeConnection(BlockPos posA, BlockPos posB) {
        return removeConnection(posA, posB, false);
    }

    @Override
    public void createNode(BlockPos pos) {
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, pos);
        createNetwork(stateBuilder.build());
    }

    private boolean removeNode(BlockPos pos, boolean dropItems) {
        NetworkNode node = networks.getNodes().get(pos);
        if (node != null) {
            Network network = node.getNetwork();
            NetworkStateBuilder state = NetworkStateBuilder.fromAbsolute(network.getState());
            state.reroot(pos);
            deleteNetwork(network);
            applySplitResult(state.splitRoot(), dropItems);

            return true;
        }

        return false;
    }

    @Override
    public final boolean breakNode(@Nullable LivingEntity entity, BlockPos pos) {
        return removeNode(pos, !Util.isCreativePlayer(entity));
    }

    @Override
    public boolean removeNode(BlockPos pos) {
        return removeNode(pos, false);
    }
}
