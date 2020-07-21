package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import net.minecraft.network.PacketByteBuf;

public class ResetConnectorStateMessage {
    public final int entityId;

    public ResetConnectorStateMessage(int entityId) {
        this.entityId = entityId;
    }

    public void serialize(PacketByteBuf buf) {
        buf.writeInt(entityId);
    }

    public static ResetConnectorStateMessage deserialize(PacketByteBuf buf) {
        return new ResetConnectorStateMessage(
            buf.readInt()
        );
    }
}
