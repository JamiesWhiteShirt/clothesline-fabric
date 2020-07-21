package com.jamieswhiteshirt.clothesline.common.util;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

public class NBTSerialization {
    public static ListTag writePersistentNetworks(List<BasicPersistentNetwork> networks) {
        ListTag nbt = new ListTag();
        for (BasicPersistentNetwork network : networks) {
            nbt.add(writePersistentNetwork(network));
        }
        return nbt;
    }

    public static List<BasicPersistentNetwork> readPersistentNetworks(ListTag nbt) {
        BasicPersistentNetwork[] networks = new BasicPersistentNetwork[nbt.size()];
        for (int i = 0; i < nbt.size(); i++) {
            networks[i] = readPersistentNetwork(nbt.getCompound(i));
        }
        return Arrays.asList(networks);
    }

    public static CompoundTag writePersistentNetwork(BasicPersistentNetwork network) {
        CompoundTag nbt = new CompoundTag();
        nbt.putUuid("Uuid", network.getUuid());
        nbt.put("State", writeNetworkState(network.getState()));
        return nbt;
    }

    public static BasicPersistentNetwork readPersistentNetwork(CompoundTag compound) {
        return new BasicPersistentNetwork(
            compound.getUuid("Uuid"),
            readNetworkState(compound.getCompound("State"))
        );
    }

    public static CompoundTag writeNetworkState(BasicNetworkState state) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Shift", state.getShift());
        nbt.putInt("Momentum", state.getMomentum());
        nbt.put("Tree", writeBasicTree(state.getTree()));
        nbt.put("Attachments", writeAttachments(state.getAttachments()));
        return nbt;
    }

    public static BasicNetworkState readNetworkState(CompoundTag nbt) {
        return new BasicNetworkState(
            nbt.getInt("Shift"),
            nbt.getInt("Momentum"),
            readBasicTree(nbt.getCompound("Tree")),
            readAttachments(nbt.getList("Attachments", NbtType.COMPOUND))
        );
    }

    public static CompoundTag writeBasicTree(BasicTree tree) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("x", tree.getPos().getX());
        nbt.putInt("y", tree.getPos().getY());
        nbt.putInt("z", tree.getPos().getZ());
        nbt.put("Children", writeBasicTreeEdges(tree.getEdges()));
        nbt.putInt("BaseRotation", tree.getBaseRotation());
        return nbt;
    }

    public static BasicTree readBasicTree(CompoundTag nbt) {
        return new BasicTree(
            new BlockPos(
                nbt.getInt("x"),
                nbt.getInt("y"),
                nbt.getInt("z")
            ),
            readBasicTreeEdges(nbt.getList("Children", NbtType.COMPOUND)),
            nbt.getInt("BaseRotation")
        );
    }

    public static ListTag writeBasicTreeEdges(List<BasicTree.Edge> edges) {
        ListTag nbt = new ListTag();
        for (BasicTree.Edge edge : edges) {
            nbt.add(writeBasicTreeEdge(edge));
        }
        return nbt;
    }

    public static List<BasicTree.Edge> readBasicTreeEdges(ListTag nbt) {
        BasicTree.Edge[] edges = new BasicTree.Edge[nbt.size()];
        for (int i = 0; i < nbt.size(); i++) {
            edges[i] = readBasicTreeEdge(nbt.getCompound(i));
        }
        return Arrays.asList(edges);
    }

    public static CompoundTag writeBasicTreeEdge(BasicTree.Edge edge) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Length", edge.getLength());
        nbt.put("Tree", writeBasicTree(edge.getTree()));
        return nbt;
    }

    public static BasicTree.Edge readBasicTreeEdge(CompoundTag nbt) {
        return new BasicTree.Edge(
            nbt.getInt("Length"),
            readBasicTree(nbt.getCompound("Tree"))
        );
    }

    public static ListTag writeAttachments(List<BasicAttachment> attachments) {
        ListTag nbt = new ListTag();
        for (BasicAttachment attachment : attachments) {
            nbt.add(writeAttachment(attachment));
        }
        return nbt;
    }

    public static List<BasicAttachment> readAttachments(ListTag nbt) {
        BasicAttachment[] attachments = new BasicAttachment[nbt.size()];
        for (int i = 0; i < nbt.size(); i++) {
            attachments[i] = readAttachment(nbt.getCompound(i));
        }
        return Arrays.asList(attachments);
    }

    public static CompoundTag writeAttachment(BasicAttachment attachment) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Offset", attachment.getKey());
        attachment.getStack().toTag(nbt);
        return nbt;
    }

    public static BasicAttachment readAttachment(CompoundTag nbt) {
        return new BasicAttachment(
            nbt.getInt("Offset"),
            ItemStack.fromTag(nbt)
        );
    }
}
