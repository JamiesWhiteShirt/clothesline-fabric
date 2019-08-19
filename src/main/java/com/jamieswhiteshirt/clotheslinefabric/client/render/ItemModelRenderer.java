package com.jamieswhiteshirt.clotheslinefabric.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.List;
import java.util.Random;

public class ItemModelRenderer {
    public static void renderModel(BakedModel bakedModel, ModelTransformation.Type modelTransformationType) {
        if (bakedModel.isBuiltin()) return;

        GlStateManager.pushMatrix();
        ModelTransformation modelTransformation = bakedModel.getTransformation();
        ModelTransformation.applyGl(modelTransformation.getTransformation(modelTransformationType), false);
        if (isInverted(modelTransformation.getTransformation(modelTransformationType))) {
            GlStateManager.cullFace(GlStateManager.FaceSides.FRONT);
        }

        GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
        renderModelColored(bakedModel, 0xFFFFFFFF);
        GlStateManager.cullFace(GlStateManager.FaceSides.BACK);
        GlStateManager.popMatrix();
    }

    private static void renderModelColored(BakedModel bakedModel, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
        Random random = new Random();
        long seed = 42L;
        Direction[] directions = Direction.values();
        int length = directions.length;

        for (Direction direction_1 : directions) {
            random.setSeed(seed);
            renderQuadsColored(bufferBuilder, bakedModel.getQuads(null, direction_1, random), color);
        }

        random.setSeed(seed);
        renderQuadsColored(bufferBuilder, bakedModel.getQuads(null, null, random), color);
        tessellator.draw();
    }

    private static void renderQuadsColored(BufferBuilder bufferBuilder_1, List<BakedQuad> list_1, int color) {
        int i = 0;
        for(int size = list_1.size(); i < size; ++i) {
            BakedQuad bakedQuad = list_1.get(i);
            renderQuadColored(bufferBuilder_1, bakedQuad, color);
        }
    }

    private static void renderQuadColored(BufferBuilder bufferBuilder, BakedQuad bakedQuad, int color) {
        bufferBuilder.putVertexData(bakedQuad.getVertexData());
        bufferBuilder.setQuadColor(color);
        Vec3i vec3i_1 = bakedQuad.getFace().getVector();
        bufferBuilder.postNormal((float)vec3i_1.getX(), (float)vec3i_1.getY(), (float)vec3i_1.getZ());
    }

    private static boolean isInverted(Transformation transformation) {
        return transformation.scale.getX() < 0.0F ^ transformation.scale.getY() < 0.0F ^ transformation.scale.getZ() < 0.0F;
    }
}
