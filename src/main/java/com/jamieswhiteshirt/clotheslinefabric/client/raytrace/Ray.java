package com.jamieswhiteshirt.clotheslinefabric.client.raytrace;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class Ray {
    public final Vec3d from;
    public final Vec3d to;
    public final Vec3d delta;
    public final double lengthSq;

    public Ray(Vec3d from, Vec3d to) {
        this.from = from;
        this.to = to;
        this.delta = to.subtract(from);
        this.lengthSq = delta.dotProduct(delta);
    }

    public Vec3d project(double scalar) {
        return from.add(delta.multiply(scalar));
    }
}
