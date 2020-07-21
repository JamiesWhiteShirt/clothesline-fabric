package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.util.PacketByteBufSerialization;
import net.minecraft.network.PacketByteBuf;

public class RemoveAttachmentMessage {
    public final int networkId;
    public final int attachmentKey;

    public RemoveAttachmentMessage(int networkId, int attachmentKey) {
        this.networkId = networkId;
        this.attachmentKey = attachmentKey;
    }

    public void serialize(PacketByteBuf buf) {
        PacketByteBufSerialization.writeNetworkId(buf, networkId);
        buf.writeInt(attachmentKey);
    }

    public static RemoveAttachmentMessage deserialize(PacketByteBuf buf) {
        return new RemoveAttachmentMessage(
            PacketByteBufSerialization.readNetworkId(buf),
            buf.readInt()
        );
    }
}
