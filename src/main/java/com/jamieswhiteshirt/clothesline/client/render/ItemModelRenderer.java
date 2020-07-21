package com.jamieswhiteshirt.clothesline.client.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Random;

public class ItemModelRenderer {
    public static void renderModel(BakedModel bakedModel, ModelTransformation.Mode modelTransformationType, MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        if (bakedModel.isBuiltin()) return;

        matrices.push();
        ModelTransformation modelTransformation = bakedModel.getTransformation();
        modelTransformation.getTransformation(modelTransformationType).apply(false, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);
        // TODO: What to do with this?
        /* if (isInverted(modelTransformation.getTransformation(modelTransformationType))) {
            RenderSystem.cullFace(GlStateManager.FaceSides.FRONT);
        } */

        renderModelColored(bakedModel, light, overlay, matrices, vertices);
        matrices.pop();
    }

    private static void renderModelColored(BakedModel bakedModel, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {
        Random random = new Random();
        long seed = 42L;
        Direction[] directions = Direction.values();

        for (Direction direction_1 : directions) {
            random.setSeed(seed);
            renderQuadsColored(matrices, vertices, bakedModel.getQuads(null, direction_1, random), light, overlay);
        }

        random.setSeed(seed);
        renderQuadsColored(matrices, vertices, bakedModel.getQuads(null, null, random), light, overlay);
    }

    private static void renderQuadsColored(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, int light, int overlay) {
        MatrixStack.Entry entry = matrices.peek();
        for (BakedQuad bakedQuad : quads) {
            vertices.quad(entry, bakedQuad, 1.0F, 1.0F, 1.0F, light, overlay);
        }
    }

    private static boolean isInverted(Transformation transformation) {
        return transformation.scale.getX() < 0.0F ^ transformation.scale.getY() < 0.0F ^ transformation.scale.getZ() < 0.0F;
    }
}
