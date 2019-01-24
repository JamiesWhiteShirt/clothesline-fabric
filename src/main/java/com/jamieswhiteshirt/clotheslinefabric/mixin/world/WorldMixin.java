package com.jamieswhiteshirt.clotheslinefabric.mixin.world;

import com.jamieswhiteshirt.clotheslinefabric.api.Line;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.rtree3i.Box;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldMixin implements ViewableWorld, NetworkManagerProvider {
    @Override
    public boolean method_8628(BlockState state, BlockPos pos) {
        if (ViewableWorld.super.method_8628(state, pos)) {
            VoxelShape shape = state.getCollisionShape(this, pos);
            if (!shape.isEmpty()) {
                BoundingBox bb = shape.offset(pos.getX(), pos.getY(), pos.getZ()).getBoundingBox();
                Box box = Box.create(
                    MathHelper.floor(bb.minX), MathHelper.floor(bb.minY), MathHelper.floor(bb.minZ),
                    MathHelper.ceil(bb.maxX), MathHelper.ceil(bb.maxY), MathHelper.ceil(bb.maxZ)
                );
                boolean intersects = getNetworkManager().getNetworks().getEdges()
                    .values(box::intersects)
                    .anyMatch(edge -> {
                        Line line = edge.getPathEdge().getLine();
                        return shape.rayTrace(line.getFromVec(), line.getToVec(), pos) != null;
                    });
                return !intersects;
            }
        }
        return true;
    }
}
