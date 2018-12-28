package com.jamieswhiteshirt.clotheslinefabric.common.network.message;

import com.jamieswhiteshirt.clotheslinefabric.common.util.BasicNetwork;
import com.jamieswhiteshirt.clotheslinefabric.common.util.PacketByteBufSerialization;
import net.minecraft.util.PacketByteBuf;

public class AddNetworkMessage {
    public final BasicNetwork network;

    public AddNetworkMessage(BasicNetwork network) {
        this.network = network;
    }

    public void serialize(PacketByteBuf buf) {
        PacketByteBufSerialization.writeNetwork(buf, network);
    }

    public static AddNetworkMessage deserialize(PacketByteBuf buf) {
        return new AddNetworkMessage(
            PacketByteBufSerialization.readNetwork(buf)
        );
    }
}
