package com.jamieswhiteshirt.clotheslinefabric.common.network;

import com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler.HitAttachmentMessageHandler;
import com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler.HitNetworkMessageHandler;
import com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler.StopUsingItemOnMessageHandler;
import com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler.TryUseItemOnNetworkMessageHandler;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class ServerMessageHandling {
    public static void init() {
        ServerSidePacketRegistry registry = ServerSidePacketRegistry.INSTANCE;
        MessageChannels.HIT_ATTACHMENT.registerHandler(registry, new HitAttachmentMessageHandler());
        MessageChannels.HIT_NETWORK.registerHandler(registry, new HitNetworkMessageHandler());
        MessageChannels.STOP_USING_ITEM_ON.registerHandler(registry, new StopUsingItemOnMessageHandler());
        MessageChannels.TRY_USE_ITEM_ON_NETWORK.registerHandler(registry, new TryUseItemOnNetworkMessageHandler());
    }
}
