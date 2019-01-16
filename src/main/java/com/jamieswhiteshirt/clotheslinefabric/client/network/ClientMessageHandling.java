package com.jamieswhiteshirt.clotheslinefabric.client.network;

import com.jamieswhiteshirt.clotheslinefabric.client.network.messagehandler.*;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;

@Environment(EnvType.CLIENT)
public class ClientMessageHandling {
    public static void init() {
        MessageChannels.ADD_NETWORK.registerHandler(CustomPayloadPacketRegistry.CLIENT, new AddNetworkMessageHandler());
        MessageChannels.REMOVE_ATTACHMENT.registerHandler(CustomPayloadPacketRegistry.CLIENT, new RemoveAttachmentMessageHandler());
        MessageChannels.REMOVE_NETWORK.registerHandler(CustomPayloadPacketRegistry.CLIENT, new RemoveNetworkMessageHandler());
        MessageChannels.RESET_CONNECTOR_STATE.registerHandler(CustomPayloadPacketRegistry.CLIENT, new ResetConnectorStateMessageHandler());
        MessageChannels.SET_ATTACHMENT.registerHandler(CustomPayloadPacketRegistry.CLIENT, new SetAttachmentMessageHandler());
        MessageChannels.SET_CONNECTOR_STATE.registerHandler(CustomPayloadPacketRegistry.CLIENT, new SetConnectorStateMessageHandler());
        MessageChannels.UPDATE_NETWORK.registerHandler(CustomPayloadPacketRegistry.CLIENT, new UpdateNetworkMessageHandler());
    }
}
