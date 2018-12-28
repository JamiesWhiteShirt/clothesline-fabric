package com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkState;
import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Validation {
    public static boolean canReachAttachment(PlayerEntity player, Network network, int attachmentKey) {
        NetworkState state = network.getState();
        return canReachPos(player, state.getPath().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey)));
    }

    public static boolean canReachPos(PlayerEntity player, Vec3d pos) {
        Vec3d delta = pos.subtract(player.x, player.y + 1.5D, player.z);
        return delta.lengthSquared() < 64.0D;
    }
}
