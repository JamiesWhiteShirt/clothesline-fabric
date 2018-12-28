package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import com.jamieswhiteshirt.clotheslinefabric.common.util.PacketByteBufSerialization;
import net.minecraft.util.PacketByteBuf;

public class UpdateNetworkMessage {
    public final int networkId;
    public final int shift;
    public final int momentum;

    public UpdateNetworkMessage(int networkId, int shift, int momentum) {
        this.networkId = networkId;
        this.shift = shift;
        this.momentum = momentum;
    }

    public void serialize(PacketByteBuf buf) {
        PacketByteBufSerialization.writeNetworkId(buf, networkId);
        buf.writeInt(shift);
        buf.writeByte(momentum);
    }

    public static UpdateNetworkMessage deserialize(PacketByteBuf buf) {
        return new UpdateNetworkMessage(
            PacketByteBufSerialization.readNetworkId(buf),
            buf.readInt(),
            buf.readByte()
        );
    }
}
