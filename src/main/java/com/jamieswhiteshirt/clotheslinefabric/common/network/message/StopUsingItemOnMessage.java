package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class StopUsingItemOnMessage {
    public final Hand hand;
    public final BlockHitResult hitResult;

    public StopUsingItemOnMessage(Hand hand, BlockHitResult hitResult) {
        this.hand = hand;
        this.hitResult = hitResult;
    }

    public void serialize(PacketByteBuf buf) {
        buf.writeEnumConstant(hand);
        buf.method_17813(hitResult);
    }

    public static StopUsingItemOnMessage deserialize(PacketByteBuf buf) {
        return new StopUsingItemOnMessage(
            buf.readEnumConstant(Hand.class),
            buf.method_17814()
        );
    }
}
