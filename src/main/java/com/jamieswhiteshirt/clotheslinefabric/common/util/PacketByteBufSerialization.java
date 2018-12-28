package com.jamieswhiteshirt.clotheslinefabric.common.util;

import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.UUID;

public class PacketByteBufSerialization {
    public static void writeNetwork(PacketByteBuf buf, BasicNetwork network) {
        writeNetworkId(buf, network.getId());
        writePersistentNetwork(buf, network.getPersistentNetwork());
    }

    public static BasicNetwork readNetwork(PacketByteBuf buf) {
        return new BasicNetwork(readNetworkId(buf), readPersistentNetwork(buf));
    }

    public static void writePersistentNetwork(PacketByteBuf buf, BasicPersistentNetwork network) {
        writeNetworkUuid(buf, network.getUuid());
        writeNetworkState(buf, network.getState());
    }

    public static BasicPersistentNetwork readPersistentNetwork(PacketByteBuf buf) {
        return new BasicPersistentNetwork(readNetworkUuid(buf), readNetworkState(buf));
    }

    public static void writeNetworkUuid(PacketByteBuf buf, UUID networkUuid) {
        buf.writeLong(networkUuid.getMostSignificantBits());
        buf.writeLong(networkUuid.getLeastSignificantBits());
    }

    public static UUID readNetworkUuid(PacketByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeNetworkId(PacketByteBuf buf, int networkId) {
        buf.writeVarInt(networkId);
    }

    public static int readNetworkId(PacketByteBuf buf) {
        return buf.readVarInt();
    }

    public static void writeNetworkState(PacketByteBuf buf, BasicNetworkState state) {
        writeBasicTree(buf, state.getTree());
        buf.writeInt(state.getShift());
        buf.writeInt(state.getMomentum());
        buf.writeShort(state.getAttachments().size());
        for (BasicAttachment attachment : state.getAttachments()) {
            buf.writeInt(attachment.getKey());
            buf.writeItemStack(attachment.getStack());
        }
    }

    public static BasicNetworkState readNetworkState(PacketByteBuf buf) {
        BasicTree tree = readBasicTree(buf);
        int offset = buf.readInt();
        int momentum = buf.readInt();
        int numAttachments = buf.readUnsignedShort();
        BasicAttachment[] attachments = new BasicAttachment[numAttachments];
        for (int i = 0; i < numAttachments; i++) {
            attachments[i] = new BasicAttachment(buf.readInt(), buf.readItemStack());
        }
        return new BasicNetworkState(
            offset,
            momentum,
            tree,
            Arrays.asList(attachments)
        );
    }

    public static void writeBasicTree(PacketByteBuf buf, BasicTree tree) {
        buf.writeLong(tree.getPos().asLong());
        buf.writeByte(tree.getEdges().size());
        for (BasicTree.Edge edge : tree.getEdges()) {
            buf.writeShort(edge.getLength());
            writeBasicTree(buf, edge.getTree());
        }
        buf.writeInt(tree.getBaseRotation());
    }

    public static BasicTree readBasicTree(PacketByteBuf buf) {
        BlockPos pos = BlockPos.fromLong(buf.readLong());
        int numChildren = buf.readUnsignedByte();
        BasicTree.Edge[] edges = new BasicTree.Edge[numChildren];
        for (int i = 0; i < numChildren; i++) {
            edges[i] = new BasicTree.Edge(
                buf.readUnsignedShort(),
                readBasicTree(buf)
            );
        }
        return new BasicTree(pos, Arrays.asList(edges), buf.readInt());
    }

    public static void writeAttachment(PacketByteBuf buf, BasicAttachment attachment) {
        buf.writeInt(attachment.getKey());
        buf.writeItemStack(attachment.getStack());
    }

    public static BasicAttachment readAttachment(PacketByteBuf buf) {
        return new BasicAttachment(
            buf.readInt(),
            buf.readItemStack()
        );
    }
}
