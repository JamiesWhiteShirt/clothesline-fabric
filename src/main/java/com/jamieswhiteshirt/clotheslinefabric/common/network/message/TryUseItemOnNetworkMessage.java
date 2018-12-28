package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import com.jamieswhiteshirt.clotheslinefabric.common.util.PacketByteBufSerialization;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class TryUseItemOnNetworkMessage {
    public final Hand hand;
    public final int networkId;
    public final int attachmentKey;

    public TryUseItemOnNetworkMessage(Hand hand, int networkId, int attachmentKey) {
        this.hand = hand;
        this.networkId = networkId;
        this.attachmentKey = attachmentKey;
    }

    public void serialize(PacketByteBuf buf) {
        buf.writeEnumConstant(hand);
        PacketByteBufSerialization.writeNetworkId(buf, networkId);
        buf.writeInt(attachmentKey);
    }

    public static TryUseItemOnNetworkMessage deserialize(PacketByteBuf buf) {
        return new TryUseItemOnNetworkMessage(
            buf.readEnumConstant(Hand.class),
            PacketByteBufSerialization.readNetworkId(buf),
            buf.readInt()
        );
    }
}
