package com.jamieswhiteshirt.clothesline.common.network.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class StopUsingItemOnMessage {
    public final Hand hand;
    public final BlockHitResult hitResult;

    public StopUsingItemOnMessage(Hand hand, BlockHitResult hitResult) {
        this.hand = hand;
        this.hitResult = hitResult;
    }

    public void serialize(PacketByteBuf buf) {
        buf.writeEnumConstant(hand);
        buf.writeBlockHitResult(hitResult);
    }

    public static StopUsingItemOnMessage deserialize(PacketByteBuf buf) {
        return new StopUsingItemOnMessage(
            buf.readEnumConstant(Hand.class),
            buf.readBlockHitResult()
        );
    }
}
