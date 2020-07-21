package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import com.jamieswhiteshirt.clotheslinefabric.common.util.PacketByteBufSerialization;
import net.minecraft.network.PacketByteBuf;

public class HitAttachmentMessage {
    public final int networkId;
    public final int attachmentKey;

    public HitAttachmentMessage(int networkId, int attachmentKey) {
        this.networkId = networkId;
        this.attachmentKey = attachmentKey;
    }

    public void serialize(PacketByteBuf buf) {
        PacketByteBufSerialization.writeNetworkId(buf, networkId);
        buf.writeInt(attachmentKey);
    }

    public static HitAttachmentMessage deserialize(PacketByteBuf buf) {
        return new HitAttachmentMessage(
            PacketByteBufSerialization.readNetworkId(buf),
            buf.readInt()
        );
    }
}
