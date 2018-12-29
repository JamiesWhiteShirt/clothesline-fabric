package com.jamieswhiteshirt.clotheslinefabric.client.render.blockentity;

import com.jamieswhiteshirt.clotheslinefabric.api.AttachmentUnit;
import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkNode;
import com.jamieswhiteshirt.clotheslinefabric.client.render.BakedModels;
import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clotheslinefabric.common.block.entity.ClotheslineAnchorBlockEntity;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class ClotheslineAnchorBlockEntityRenderer extends BlockEntityRenderer<ClotheslineAnchorBlockEntity> {
    private final MinecraftClient client;

    public ClotheslineAnchorBlockEntityRenderer() {
        client = MinecraftClient.getInstance();
    }

    @Override
    public void render(ClotheslineAnchorBlockEntity te, double x, double y, double z, float delta, int destroyStage) {
        NetworkNode node = te.getNetworkNode();
        float crankRotation = 0.0F;
        if (node != null) {
            Network network = node.getNetwork();
            float shift = network.getState().getShift() * delta + network.getState().getPreviousShift() * (1.0F - delta);
            crankRotation = -(node.getPathNode().getBaseRotation() + shift) * 360.0F / AttachmentUnit.UNITS_PER_BLOCK;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5D, y + 0.5D, z + 0.5D);
        if (te.getCachedState().get(ClotheslineAnchorBlock.field_11007) == WallMountLocation.CEILING) {
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            crankRotation = -crankRotation;
        }
        GlStateManager.rotatef(crankRotation, 0.0F, 1.0F, 0.0F);

        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0F, 2.0F, 2.0F);
        renderModel(BakedModels.pulleyWheel, ModelTransformation.Type.FIXED);
        if (node != null && !node.getNetwork().getState().getTree().isEmpty()) {
            renderModel(BakedModels.pulleyWheelRope, ModelTransformation.Type.FIXED);
        }
        GlStateManager.popMatrix();

        if (te.getHasCrank()) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 4.0F / 16.0F, 0.0F);
            client.getItemRenderer().renderItemWithTransformation(new ItemStack(ClotheslineItems.CRANK), ModelTransformation.Type.FIXED);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    private void renderModel(BakedModel bakedModel, ModelTransformation.Type modelTransformationType) {
        if (bakedModel.isBuiltin()) return;

        client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO);
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
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
    }

    private void renderModelColored(BakedModel bakedModel, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
        Random random = new Random();
        long seed = 42L;
        Direction[] directions = Direction.values();
        int length = directions.length;

        for(int i = 0; i < length; ++i) {
            Direction direction_1 = directions[i];
            random.setSeed(seed);
            renderQuadsColored(bufferBuilder, bakedModel.getQuads(null, direction_1, random), color);
        }

        random.setSeed(seed);
        renderQuadsColored(bufferBuilder, bakedModel.getQuads(null, null, random), color);
        tessellator.draw();
    }

    private void renderQuadsColored(BufferBuilder bufferBuilder_1, List<BakedQuad> list_1, int color) {
        int i = 0;
        for(int size = list_1.size(); i < size; ++i) {
            BakedQuad bakedQuad = list_1.get(i);
            renderQuadColored(bufferBuilder_1, bakedQuad, color);
        }
    }

    private void renderQuadColored(BufferBuilder bufferBuilder, BakedQuad bakedQuad, int color) {
        bufferBuilder.putVertexData(bakedQuad.getVertexData());
        bufferBuilder.setQuadColor(color);
        Vec3i vec3i_1 = bakedQuad.getFace().getVector();
        bufferBuilder.postNormal((float)vec3i_1.getX(), (float)vec3i_1.getY(), (float)vec3i_1.getZ());
    }

    private boolean isInverted(Transformation transformation) {
        return transformation.scale.x() < 0.0F ^ transformation.scale.y() < 0.0F ^ transformation.scale.z() < 0.0F;
    }
}
