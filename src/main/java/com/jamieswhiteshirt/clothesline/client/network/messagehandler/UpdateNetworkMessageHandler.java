package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.common.network.message.UpdateNetworkMessage;
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
