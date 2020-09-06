package com.jamieswhiteshirt.clothesline.mixin.world;

import com.jamieswhiteshirt.clothesline.api.Line;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldMixin implements CollisionView, NetworkManagerProvider {
    @Override
    public boolean canPlace(BlockState state, BlockPos pos, ShapeContext shapeContext) {
        if (CollisionView.super.canPlace(state, pos, shapeContext)) {
            VoxelShape shape = state.getCollisionShape(this, pos);
            if (!shape.isEmpty()) {
                net.minecraft.util.math.Box bb = shape.offset(pos.getX(), pos.getY(), pos.getZ()).getBoundingBox();
                com.jamieswhiteshirt.rtree3i.Box box = com.jamieswhiteshirt.rtree3i.Box.create(
                    MathHelper.floor(bb.minX), MathHelper.floor(bb.minY), MathHelper.floor(bb.minZ),
                    MathHelper.ceil(bb.maxX), MathHelper.ceil(bb.maxY), MathHelper.ceil(bb.maxZ)
                );
                boolean intersects = getNetworkManager().getNetworks().getEdges()
                    .values(box::intersectsClosed)
                    .anyMatch(edge -> {
                        Line line = edge.getPathEdge().getLine();
                        return shape.raycast(line.getFromVec(), line.getToVec(), pos) != null;
                    });
                return !intersects;
            }
            return true;
        }
        return false;
    }
}
