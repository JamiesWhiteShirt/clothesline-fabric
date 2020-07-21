package com.jamieswhiteshirt.clothesline.common.network;

import com.jamieswhiteshirt.clothesline.common.network.message.*;
import net.minecraft.util.Identifier;

public class MessageChannels {
    public static final MessageChannel<AddNetworkMessage> ADD_NETWORK = new MessageChannel<>(
        new Identifier("clothesline", "add_network"), AddNetworkMessage::serialize, AddNetworkMessage::deserialize
    );
    public static final MessageChannel<HitAttachmentMessage> HIT_ATTACHMENT = new MessageChannel<>(
        new Identifier("clothesline", "hit_attachment"), HitAttachmentMessage::serialize, HitAttachmentMessage::deserialize
    );
    public static final MessageChannel<HitNetworkMessage> HIT_NETWORK = new MessageChannel<>(
        new Identifier("clothesline", "hit_network"), HitNetworkMessage::serialize, HitNetworkMessage::deserialize
    );
    public static final MessageChannel<RemoveAttachmentMessage> REMOVE_ATTACHMENT = new MessageChannel<>(
        new Identifier("clothesline", "remove_attachment"), RemoveAttachmentMessage::serialize, RemoveAttachmentMessage::deserialize
    );
    public static final MessageChannel<RemoveNetworkMessage> REMOVE_NETWORK = new MessageChannel<>(
        new Identifier("clothesline", "remove_network"), RemoveNetworkMessage::serialize, RemoveNetworkMessage::deserialize
    );
    public static final MessageChannel<ResetConnectorStateMessage> RESET_CONNECTOR_STATE = new MessageChannel<>(
        new Identifier("clothesline", "reset_connector_state"), ResetConnectorStateMessage::serialize, ResetConnectorStateMessage::deserialize
    );
    public static final MessageChannel<SetAttachmentMessage> SET_ATTACHMENT = new MessageChannel<>(
        new Identifier("clothesline", "set_attachment"), SetAttachmentMessage::serialize, SetAttachmentMessage::deserialize
    );
    public static final MessageChannel<SetConnectorStateMessage> SET_CONNECTOR_STATE = new MessageChannel<>(
        new Identifier("clothesline", "set_connector_state"), SetConnectorStateMessage::serialize, SetConnectorStateMessage::deserialize
    );
    public static final MessageChannel<StopUsingItemOnMessage> STOP_USING_ITEM_ON = new MessageChannel<>(
        new Identifier("clothesline", "stop_using_item_on"), StopUsingItemOnMessage::serialize, StopUsingItemOnMessage::deserialize
    );
    public static final MessageChannel<TryUseItemOnNetworkMessage> TRY_USE_ITEM_ON_NETWORK = new MessageChannel<>(
        new Identifier("clothesline", "try_use_item_on_network"), TryUseItemOnNetworkMessage::serialize, TryUseItemOnNetworkMessage::deserialize
    );
    public static final MessageChannel<UpdateNetworkMessage> UPDATE_NETWORK = new MessageChannel<>(
        new Identifier("clothesline", "update_network"), UpdateNetworkMessage::serialize, UpdateNetworkMessage::deserialize
    );
}
