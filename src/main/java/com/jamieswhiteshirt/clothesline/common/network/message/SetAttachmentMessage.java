package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.PacketByteBufSerialization;
import net.minecraft.network.PacketByteBuf;

public class SetAttachmentMessage {
    public final int networkId;
    public final BasicAttachment attachment;

    public SetAttachmentMessage(int networkId, BasicAttachment attachment) {
        this.networkId = networkId;
        this.attachment = attachment;
    }

    public void serialize(PacketByteBuf buf) {
        PacketByteBufSerialization.writeNetworkId(buf, networkId);
        PacketByteBufSerialization.writeAttachment(buf, attachment);
    }

    public static SetAttachmentMessage deserialize(PacketByteBuf buf) {
        return new SetAttachmentMessage(
            PacketByteBufSerialization.readNetworkId(buf),
            PacketByteBufSerialization.readAttachment(buf)
        );
    }
}
