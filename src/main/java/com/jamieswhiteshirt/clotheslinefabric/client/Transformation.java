package com.jamieswhiteshirt.clotheslinefabric.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public final class Transformation {
    private final Matrix4f model;
    private final Matrix3f normal;

    public Transformation(Matrix4f model, Matrix3f normal) {
        this.model = model;
        this.normal = normal;
    }

    public Matrix4f getModel() {
        return model;
    }

    public Matrix3f getNormal() {
        return normal;
    }

    public void apply(MatrixStack matrices) {
        matrices.peek().getModel().multiply(model);
        matrices.peek().getNormal().multiply(normal);
    }
}
