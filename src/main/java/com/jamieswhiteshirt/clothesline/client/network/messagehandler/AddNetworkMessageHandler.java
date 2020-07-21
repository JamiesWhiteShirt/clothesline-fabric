package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.common.network.message.AddNetworkMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class AddNetworkMessageHandler implements BiConsumer<PacketContext, AddNetworkMessage> {
    @Override
    public void accept(PacketContext ctx, AddNetworkMessage msg) {
        NetworkManager manager = ((NetworkManagerProvider) ctx.getPlayer().world).getNetworkManager();
        manager.getNetworks().add(msg.network.toAbsolute());
    }
}
