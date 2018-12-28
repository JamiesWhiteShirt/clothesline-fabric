package com.jamieswhiteshirt.clotheslinefabric.util;

import com.jamieswhiteshirt.clotheslinefabric.common.util.BasicNetwork;
import com.jamieswhiteshirt.clotheslinefabric.common.util.PacketByteBufSerialization;
import io.netty.buffer.Unpooled;
import net.minecraft.util.PacketByteBuf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PacketByteBufSerializationTest {
    @Test
    void persistsNetworkEquality() {
        BasicNetwork written = BasicNetwork.fromAbsolute(NetworkTests.ab.network);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        PacketByteBufSerialization.writeNetwork(buf, written);
        BasicNetwork read = PacketByteBufSerialization.readNetwork(buf);
        Assertions.assertEquals(written, read);
    }
}
