package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StopUsingItemOnMessage {
    public final BlockPos pos;
    public final Direction direction;
    public final Hand hand;
    public final float hitX;
    public final float hitY;
    public final float hitZ;

    public StopUsingItemOnMessage(BlockPos pos, Direction direction, Hand hand, float hitX, float hitY, float hitZ) {
        this.pos = pos;
        this.direction = direction;
        this.hand = hand;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    public void serialize(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(direction);
        buf.writeEnumConstant(hand);
        buf.writeFloat(hitX);
        buf.writeFloat(hitY);
        buf.writeFloat(hitZ);
    }

    public static StopUsingItemOnMessage deserialize(PacketByteBuf buf) {
        return new StopUsingItemOnMessage(
            buf.readBlockPos(),
            buf.readEnumConstant(Direction.class),
            buf.readEnumConstant(Hand.class),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat()
        );
    }
}
