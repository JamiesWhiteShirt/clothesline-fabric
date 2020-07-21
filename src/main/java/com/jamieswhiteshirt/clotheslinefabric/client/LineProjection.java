package com.jamieswhiteshirt.clotheslinefabric.client;

import com.jamieswhiteshirt.clotheslinefabric.api.Line;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkEdge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class LineProjection {
    private final Vec3d origin;
    private final Vec3d right;
    private final Vec3d up;
    private final Vec3d forward;

    public LineProjection(Vec3d origin, Vec3d right, Vec3d up, Vec3d forward) {
        this.origin = origin;
        this.right = right;
        this.up = up;
        this.forward = forward;
    }

    public Vec3d projectRUF(float r, float u, float f) {
        return origin.add(right.multiply(r)).add(up.multiply(u)).add(forward.multiply(f));
    }

    public Transformation getTransformation() {
        Matrix4f model = new Matrix4f();
        ((Matrix4fExtension) (Object) model).load(
            (float) right.x, (float) up.x, (float) forward.x, (float) origin.x,
            (float) right.y, (float) up.y, (float) forward.y, (float) origin.y,
            (float) right.z, (float) up.z, (float) forward.z, (float) origin.z,
            0.0F, 0.0F, 0.0F, 1.0F
        );
        Matrix3f normal = new Matrix3f();
        ((Matrix3fExtension) (Object) normal).load(
            (float) right.x, (float) up.x, (float) forward.x,
            (float) right.y, (float) up.y, (float) forward.y,
            (float) right.z, (float) up.z, (float) forward.z
        );

        return new Transformation(model, normal);
    }

    public static LineProjection create(Vec3d from, Vec3d to) {
        // The normal vector facing from the from pos to the to pos
        Vec3d forward = to.subtract(from).normalize();
        // The normal vector facing right to the forward normal (on the y plane)
        Vec3d rightNormal = forward.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        if (rightNormal.equals(Vec3d.ZERO)) {
            // We are looking straight up or down so the right normal is undefined
            // Let it be x if we are looking straight up or -x if we are looking straight down
            rightNormal = new Vec3d(Math.signum(forward.y), 0.0D, 0.0D);
        }
        // The normal vector facing up from the forward normal (on the right normal plane)
        Vec3d upNormal = rightNormal.crossProduct(forward);

        return new LineProjection(from, rightNormal, upNormal, forward);
    }

    public static LineProjection create(Line line) {
        return create(line.getFromVec(), line.getToVec());
    }

    public static LineProjection create(NetworkEdge edge) {
        return create(edge.getPathEdge().getLine());
    }

    public static Transformation createTransformation(Vec3d from, Vec3d to) {
        // The normal vector facing from the from pos to the to pos
        Vec3d forward = to.subtract(from).normalize();
        // The normal vector facing right to the forward normal (on the y plane)
        Vec3d rightNormal = forward.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        if (rightNormal.equals(Vec3d.ZERO)) {
            // We are looking straight up or down so the right normal is undefined
            // Let it be x if we are looking straight up or -x if we are looking straight down
            rightNormal = new Vec3d(Math.signum(forward.y), 0.0D, 0.0D);
        }
        // The normal vector facing up from the forward normal (on the right normal plane)
        Vec3d upNormal = rightNormal.crossProduct(forward);

        Matrix4f model = new Matrix4f();
        ((Matrix4fExtension) (Object) model).load(
            (float) rightNormal.x, (float) upNormal.x, (float) forward.x, (float) from.x,
            (float) rightNormal.y, (float) upNormal.y, (float) forward.y, (float) from.y,
            (float) rightNormal.z, (float) upNormal.z, (float) forward.z, (float) from.z,
            0.0F, 0.0F, 0.0F, 1.0F
        );
        Matrix3f normal = new Matrix3f();
        ((Matrix3fExtension) (Object) normal).load(
            (float) rightNormal.x, (float) upNormal.x, (float) forward.x,
            (float) rightNormal.y, (float) upNormal.y, (float) forward.y,
            (float) rightNormal.z, (float) upNormal.z, (float) forward.z
        );

        return new Transformation(model, normal);
    }

    public static Transformation createTransformation(Line line) {
        return createTransformation(line.getFromVec(), line.getToVec());
    }

    public static Transformation createTransformation(NetworkEdge edge) {
        return createTransformation(edge.getPathEdge().getLine());
    }
}
