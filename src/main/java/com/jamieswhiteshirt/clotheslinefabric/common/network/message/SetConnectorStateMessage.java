package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class SetConnectorStateMessage {
    public final int entityId;
    public final Hand hand;
    public final BlockHitResult hitResult;

    public SetConnectorStateMessage(int entityId, Hand hand, BlockHitResult hitResult) {
        this.entityId = entityId;
        this.hand = hand;
        this.hitResult = hitResult;
    }

    public void serialize(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeEnumConstant(hand);
        buf.method_17813(hitResult);
    }

    public static SetConnectorStateMessage deserialize(PacketByteBuf buf) {
        return new SetConnectorStateMessage(
            buf.readInt(),
            buf.readEnumConstant(Hand.class),
            buf.method_17814()
        );
    }
}
