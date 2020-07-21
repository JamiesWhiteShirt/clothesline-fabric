package com.jamieswhiteshirt.clothesline.client.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAttachmentMessage;
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
