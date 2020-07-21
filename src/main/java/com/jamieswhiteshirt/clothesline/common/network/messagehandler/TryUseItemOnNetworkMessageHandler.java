package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.common.network.MessageChannels;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.TryUseItemOnNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.BiConsumer;

public class TryUseItemOnNetworkMessageHandler implements BiConsumer<PacketContext, TryUseItemOnNetworkMessage> {
    @Override
    public void accept(PacketContext ctx, TryUseItemOnNetworkMessage message) {
        ServerPlayerEntity player = (ServerPlayerEntity) ctx.getPlayer();
        NetworkManager manager = ((NetworkManagerProvider) player.world).getNetworkManager();
        Network network = manager.getNetworks().getById(message.networkId);
        if (network != null) {
            if (Validation.canReachAttachment(player, network, message.attachmentKey)) {
                network.useItem(player, message.hand, message.attachmentKey);
            }

            // The client may have made an incorrect assumption.
            // Send the current attachment to make sure the client keeps up.
            ItemStack stack = network.getState().getAttachment(message.attachmentKey);
            if (!stack.isEmpty()) {
                player.networkHandler.sendPacket(MessageChannels.SET_ATTACHMENT.createClientboundPacket(new SetAttachmentMessage(
                    network.getId(),
                    new BasicAttachment(message.attachmentKey, stack)
                )));
            } else {
                player.networkHandler.sendPacket(MessageChannels.REMOVE_ATTACHMENT.createClientboundPacket(new RemoveAttachmentMessage(
                    network.getId(),
                    message.attachmentKey
                )));
            }
        }
    }
}
