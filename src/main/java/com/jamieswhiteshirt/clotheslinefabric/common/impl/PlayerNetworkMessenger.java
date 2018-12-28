package com.jamieswhiteshirt.clotheslinefabric.common.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.*;
import com.jamieswhiteshirt.clotheslinefabric.common.util.BasicAttachment;
import com.jamieswhiteshirt.clotheslinefabric.common.util.BasicNetwork;
import com.jamieswhiteshirt.clotheslinefabric.internal.NetworkMessenger;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerNetworkMessenger implements NetworkMessenger<ServerPlayerEntity> {
    @Override
    public void addNetwork(ServerPlayerEntity watcher, Network network) {
        watcher.networkHandler.sendPacket(MessageChannels.ADD_NETWORK.createClientboundPacket(
            new AddNetworkMessage(BasicNetwork.fromAbsolute(network))
        ));
    }

    @Override
    public void removeNetwork(ServerPlayerEntity watcher, Network network) {
        watcher.networkHandler.sendPacket(MessageChannels.REMOVE_NETWORK.createClientboundPacket(
            new RemoveNetworkMessage(network.getId())
        ));
    }

    @Override
    public void setAttachment(ServerPlayerEntity watcher, Network network, int attachmentKey, ItemStack stack) {
        if (stack.isEmpty()) {
            watcher.networkHandler.sendPacket(MessageChannels.REMOVE_ATTACHMENT.createClientboundPacket(
                new RemoveAttachmentMessage(network.getId(), attachmentKey)
            ));
        } else {
            watcher.networkHandler.sendPacket(MessageChannels.SET_ATTACHMENT.createClientboundPacket(
                new SetAttachmentMessage(network.getId(), new BasicAttachment(attachmentKey, stack))
            ));
        }
    }

    @Override
    public void setShiftAndMomentum(ServerPlayerEntity watcher, Network network, int shift, int momentum) {
        watcher.networkHandler.sendPacket(MessageChannels.UPDATE_NETWORK.createClientboundPacket(
            new UpdateNetworkMessage(network.getId(), shift, momentum)
        ));
    }
}
