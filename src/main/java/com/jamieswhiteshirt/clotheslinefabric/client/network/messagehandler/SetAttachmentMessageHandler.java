package com.jamieswhiteshirt.clotheslinefabric.client.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.SetAttachmentMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class SetAttachmentMessageHandler implements BiConsumer<PacketContext, SetAttachmentMessage> {
    @Override
    public void accept(PacketContext ctx, SetAttachmentMessage msg) {
        NetworkManager manager = ((NetworkManagerProvider) ctx.getPlayer().world).getNetworkManager();
        Network network = manager.getNetworks().getById(msg.networkId);
        if (network != null) {
            network.setAttachment(msg.attachment.getKey(), msg.attachment.getStack());
        }
    }
}
