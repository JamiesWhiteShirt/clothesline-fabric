package com.jamieswhiteshirt.clotheslinefabric.client.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.UpdateNetworkMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class UpdateNetworkMessageHandler implements BiConsumer<PacketContext, UpdateNetworkMessage> {
    @Override
    public void accept(PacketContext ctx, UpdateNetworkMessage msg) {
        NetworkManager manager = ((NetworkManagerProvider) ctx.getPlayer().world).getNetworkManager();
        Network network = manager.getNetworks().getById(msg.networkId);
        if (network != null) {
            network.getState().setShift(msg.shift);
            network.getState().setMomentum(msg.momentum);
        }
    }
}
