package com.jamieswhiteshirt.clotheslinefabric.client;

import com.jamieswhiteshirt.clotheslinefabric.api.AttachmentUnit;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkEdge;
import com.jamieswhiteshirt.clotheslinefabric.api.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class EdgeAttachmentProjector {
    private final int fromOffset;
    private final int toOffset;
    private final LineProjection projection;
    private final float angleY;
    private final float fromAngleDiff;
    private final float toAngleDiff;

    private EdgeAttachmentProjector(int fromOffset, int toOffset, LineProjection projection, float angleY, float fromAngleDiff, float toAngleDiff) {
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.projection = projection;
        this.angleY = angleY;
        this.fromAngleDiff = fromAngleDiff;
        this.toAngleDiff = toAngleDiff;
    }

    private static float floorModAngle(float angle) {
        if (angle >= 0.0F) {
            return angle % 360.0F;
        } else {
            return 360.0F + (angle % 360.0F);
        }
    }

    private static float calculateGlobalAngleY(BlockPos delta) {
        return floorModAngle((float)Math.toDegrees(Math.atan2(delta.getZ(), delta.getX())));
    }


    private static float angleBetween(Path.Edge a, Path.Edge b) {
        float angleA = calculateGlobalAngleY(a.getDelta());
        float angleB = calculateGlobalAngleY(b.getDelta());
        return floorModAngle(angleA - angleB);
    }

    public static EdgeAttachmentProjector build(Path.Edge fromPathEdge, Path.Edge pathEdge, Path.Edge toPathEdge, LineProjection projection) {
        return new EdgeAttachmentProjector(
            pathEdge.getFromOffset(),
            pathEdge.getToOffset(),
            projection,
            calculateGlobalAngleY(pathEdge.getDelta()),
            angleBetween(fromPathEdge, pathEdge),
            angleBetween(pathEdge, toPathEdge)
        );
    }

    public static EdgeAttachmentProjector build(NetworkEdge edge) {
        int index = edge.getIndex();
        List<Path.Edge> edges = edge.getNetwork().getState().getPath().getEdges();
        Path.Edge fromPathEdge = edges.get(Math.floorMod(index - 1, edges.size()));
        Path.Edge toPathEdge = edges.get(Math.floorMod(index + 1, edges.size()));
        return EdgeAttachmentProjector.build(fromPathEdge, edge.getPathEdge(), toPathEdge, LineProjection.create(edge));
    }

    private float calculateSwingAngle(double momentum, double offset) {
        if (momentum == 0.0D) {
            return 0.0F;
        }
        double t;
        double angleDiff;
        if (momentum > 0.0D) {
            t = offset - fromOffset;
            angleDiff = fromAngleDiff;
        } else {
            t = toOffset - offset;
            angleDiff = toAngleDiff;
        }
        float speedRatio = (float) momentum / AttachmentUnit.UNITS_PER_BLOCK;
        float swingMax = 6.0F * (float) angleDiff * speedRatio * speedRatio;

        return swingMax *
            (float)(Math.exp(-t / (AttachmentUnit.UNITS_PER_BLOCK * 2.0D))) *
            MathHelper.sin((float)(Math.PI * t / AttachmentUnit.UNITS_PER_BLOCK));
    }

    public Mat4f getL2WForAttachment(double momentum, double offset, float delta) {
        double relativeOffset = offset - fromOffset;
        double edgePosScalar = relativeOffset / (toOffset - fromOffset);
        Vec3d pos = projection.projectRUF(-2.0D / 16.0D, 0.0D, edgePosScalar);
        float swingAngle = calculateSwingAngle(momentum, offset);

        Mat4f result = Mat4f.translate((float) pos.x, (float) pos.y, (float) pos.z);
        result.multiply(Mat4f.scale(0.5F, 0.5F, 0.5F));
        result.multiply(Mat4f.rotateY((float) Math.toRadians(-angleY)));
        result.multiply(Mat4f.rotateX((float) Math.toRadians(swingAngle)));
        result.multiply(Mat4f.translate(0.0F, -0.5F, 0.0F));
        return result;
    }

    public Mat4f getW2LForAttachment(double momentum, double offset, float delta) {
        double relativeOffset = offset - fromOffset;
        double edgePosScalar = relativeOffset / (toOffset - fromOffset);
        Vec3d pos = projection.projectRUF(-2.0D / 16.0D, 0.0D, edgePosScalar);
        float swingAngle = calculateSwingAngle(momentum, offset);

        Mat4f result = Mat4f.translate(0.0F, 0.5F, 0.0F);
        result.multiply(Mat4f.rotateX((float) Math.toRadians(-swingAngle)));
        result.multiply(Mat4f.rotateY((float) Math.toRadians(angleY)));
        result.multiply(Mat4f.scale(2.0F, 2.0F, 2.0F));
        result.multiply(Mat4f.translate((float) -pos.x, (float) -pos.y, (float) -pos.z));
        return result;
    }
}
