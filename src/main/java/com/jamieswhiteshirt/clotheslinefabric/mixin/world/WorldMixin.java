package com.jamieswhiteshirt.clotheslinefabric.mixin.world;

import com.jamieswhiteshirt.clotheslinefabric.api.Line;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldMixin implements CollisionView, NetworkManagerProvider {
    @Override
    public boolean canPlace(BlockState state, BlockPos pos, EntityContext entityContext) {
        if (CollisionView.super.canPlace(state, pos, entityContext)) {
            VoxelShape shape = state.getCollisionShape(this, pos);
            if (!shape.isEmpty()) {
                net.minecraft.util.math.Box bb = shape.offset(pos.getX(), pos.getY(), pos.getZ()).getBoundingBox();
                com.jamieswhiteshirt.rtree3i.Box box = com.jamieswhiteshirt.rtree3i.Box.create(
                    MathHelper.floor(bb.x1), MathHelper.floor(bb.y1), MathHelper.floor(bb.z1),
                    MathHelper.ceil(bb.x2), MathHelper.ceil(bb.y2), MathHelper.ceil(bb.z2)
                );
                boolean intersects = getNetworkManager().getNetworks().getEdges()
                    .values(box::intersectsClosed)
                    .anyMatch(edge -> {
                        Line line = edge.getPathEdge().getLine();
                        return shape.rayTrace(line.getFromVec(), line.getToVec(), pos) != null;
                    });
                return !intersects;
            }
            return true;
        }
        return false;
    }
}
