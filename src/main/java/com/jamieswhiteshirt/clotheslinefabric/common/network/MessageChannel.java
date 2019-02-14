package com.jamieswhiteshirt.clotheslinefabric.common.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class MessageChannel<T> {
    private final Identifier id;
    private final BiConsumer<T, PacketByteBuf> serializer;
    private final Function<PacketByteBuf, T> deserializer;

    public MessageChannel(Identifier id, BiConsumer<T, PacketByteBuf> serializer, Function<PacketByteBuf, T> deserializer) {
        this.id = id;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public void registerHandler(PacketRegistry registry, BiConsumer<PacketContext, T> handler) {
        registry.register(id, (ctx, buf) -> {
            T msg = deserializer.apply(buf);
            ctx.getTaskQueue().execute(() -> handler.accept(ctx, msg));
        });
    }

    public CustomPayloadS2CPacket createClientboundPacket(T msg) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        serializer.accept(msg, buf);
        return new CustomPayloadS2CPacket(id, buf);
    }

    public CustomPayloadC2SPacket createServerboundPacket(T msg) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        serializer.accept(msg, buf);
        return new CustomPayloadC2SPacket(id, buf);
    }
}
