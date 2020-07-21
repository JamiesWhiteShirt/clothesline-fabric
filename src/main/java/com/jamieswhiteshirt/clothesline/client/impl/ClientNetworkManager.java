package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.NetworkCollection;
import com.jamieswhiteshirt.clothesline.api.NetworkState;
import com.jamieswhiteshirt.clothesline.common.impl.CommonNetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public final class ClientNetworkManager extends CommonNetworkManager {
    public ClientNetworkManager(ClientWorld world, NetworkCollection networks) {
        super(world, networks);
    }

    @Override
    protected void createNetwork(NetworkState networkState) {
    }

    @Override
    protected void deleteNetwork(Network network) {
    }

    @Override
    protected void dropItems(NetworkState state, boolean dropClotheslines) {
    }
}
