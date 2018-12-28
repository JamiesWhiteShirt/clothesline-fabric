package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class SetAnchorHasCrankMessage {
    public final BlockPos pos;
    public final boolean hasCrank;

    public SetAnchorHasCrankMessage(BlockPos pos, boolean hasCrank) {
        this.pos = pos;
        this.hasCrank = hasCrank;
    }

    public void serialize(PacketByteBuf buf) {
        buf.writeLong(pos.asLong());
        buf.writeBoolean(hasCrank);
    }

    public static SetAnchorHasCrankMessage deserialize(PacketByteBuf buf) {
        return new SetAnchorHasCrankMessage(
            BlockPos.fromLong(buf.readLong()),
            buf.readBoolean()
        );
    }
}
