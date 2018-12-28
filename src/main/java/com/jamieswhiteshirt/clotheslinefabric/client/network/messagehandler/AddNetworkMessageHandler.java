package com.jamieswhiteshirt.clotheslinefabric.client.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.AddNetworkMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.networking.PacketContext;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class AddNetworkMessageHandler implements BiConsumer<PacketContext, AddNetworkMessage> {
    @Override
    public void accept(PacketContext ctx, AddNetworkMessage msg) {
        NetworkManager manager = ((NetworkManagerProvider) ctx.getPlayer().world).getNetworkManager();
        manager.getNetworks().add(msg.network.toAbsolute());
    }
}
