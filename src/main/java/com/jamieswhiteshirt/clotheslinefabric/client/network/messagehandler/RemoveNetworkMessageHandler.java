package com.jamieswhiteshirt.clotheslinefabric.client.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.RemoveNetworkMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.networking.PacketContext;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class RemoveNetworkMessageHandler implements BiConsumer<PacketContext, RemoveNetworkMessage> {
    @Override
    public void accept(PacketContext ctx, RemoveNetworkMessage msg) {
        NetworkManager manager = ((NetworkManagerProvider) ctx.getPlayer().world).getNetworkManager();
        manager.getNetworks().removeById(msg.networkId);
    }
}
