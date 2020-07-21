package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.common.util.PacketByteBufSerialization;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
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
