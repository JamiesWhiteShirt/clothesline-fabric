package com.jamieswhiteshirt.clotheslinefabric.common.network;

import com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler.HitAttachmentMessageHandler;
import com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler.HitNetworkMessageHandler;
import com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler.StopUsingItemOnMessageHandler;
import com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler.TryUseItemOnNetworkMessageHandler;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;

public class ServerMessageHandling {
    public static void init() {
        MessageChannels.HIT_ATTACHMENT.registerHandler(CustomPayloadPacketRegistry.SERVER, new HitAttachmentMessageHandler());
        MessageChannels.HIT_NETWORK.registerHandler(CustomPayloadPacketRegistry.SERVER, new HitNetworkMessageHandler());
        MessageChannels.STOP_USING_ITEM_ON.registerHandler(CustomPayloadPacketRegistry.SERVER, new StopUsingItemOnMessageHandler());
        MessageChannels.TRY_USE_ITEM_ON_NETWORK.registerHandler(CustomPayloadPacketRegistry.SERVER, new TryUseItemOnNetworkMessageHandler());
    }
}
