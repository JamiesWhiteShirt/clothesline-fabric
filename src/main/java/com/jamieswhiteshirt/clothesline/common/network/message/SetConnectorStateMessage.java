package com.jamieswhiteshirt.clothesline.common.network.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

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
        buf.writeBlockHitResult(hitResult);
    }

    public static SetConnectorStateMessage deserialize(PacketByteBuf buf) {
        return new SetConnectorStateMessage(
            buf.readInt(),
            buf.readEnumConstant(Hand.class),
            buf.readBlockHitResult()
        );
    }
}
