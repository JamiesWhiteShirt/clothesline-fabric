package com.jamieswhiteshirt.clotheslinefabric.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Utility {
    public static Vec3d midVec(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }
}
