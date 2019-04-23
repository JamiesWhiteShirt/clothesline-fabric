package com.jamieswhiteshirt.clotheslinefabric.common.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkCollection;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkState;
import com.jamieswhiteshirt.clotheslinefabric.api.Tree;
import com.jamieswhiteshirt.clotheslinefabric.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clotheslinefabric.internal.NetworkProvider;
import com.jamieswhiteshirt.clotheslinefabric.internal.PersistentNetwork;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public final class ServerNetworkManager extends CommonNetworkManager {
    private final ServerWorld world;
    private final NetworkProvider provider;

    public ServerNetworkManager(ServerWorld world, NetworkCollection networks, NetworkProvider provider) {
        super(world, networks);
        this.world = world;
        this.provider = provider;
    }

    private void dropAttachment(NetworkState state, ItemStack stack, int attachmentKey) {
        if (!stack.isEmpty()) {
            Vec3d pos = state.getPath().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey));
            ItemEntity itemEntity = new ItemEntity(world, pos.x, pos.y - 0.5D, pos.z, stack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }

    private void dropTreeItems(Tree tree) {
        BlockPos from = tree.getPos();
        for (Tree.Edge edge : tree.getEdges()) {
            BlockPos to = edge.getTree().getPos();
            ItemEntity itemEntity = new ItemEntity(
                world,
                (1 + from.getX() + to.getX()) / 2.0D,
                (1 + from.getY() + to.getY()) / 2.0D,
                (1 + from.getZ() + to.getZ()) / 2.0D,
                new ItemStack(ClotheslineItems.CLOTHESLINE)
            );
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
            dropTreeItems(edge.getTree());
        }
    }

    @Override
    protected void dropItems(NetworkState state, boolean dropClotheslines) {
        if (world.getGameRules().getBoolean("doTileDrops")) {
            for (MutableSortedIntMap.Entry<ItemStack> entry : state.getAttachments().entries()) {
                dropAttachment(state, entry.getValue(), entry.getKey());
            }
            if (dropClotheslines) {
                dropTreeItems(state.getTree());
            }
        }
    }

    @Override
    protected void createNetwork(NetworkState state) {
        provider.addNetwork(new PersistentNetwork(UUID.randomUUID(), state));
    }

    @Override
    protected void deleteNetwork(Network network) {
        provider.removeNetwork(network.getUuid());
    }
}
