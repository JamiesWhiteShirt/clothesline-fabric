package com.jamieswhiteshirt.clothesline.mixin.client.gui.hud;

import com.jamieswhiteshirt.clothesline.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    private static final Identifier CLOTHESLINE_GUI_ICONS = new Identifier("clothesline", "textures/gui/icons.png");
    private static final int CLOTHESLINE_ICONS_WIDTH = 32, CLOTHESLINE_ICONS_HEIGHT = 16;

    private static void drawTexture(MatrixStack matrices, float x, float y, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        Matrix4f matrix = matrices.peek().getModel();
        float uScale = 1.0F / textureWidth;
        float vScale = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x, y + regionHeight, 0.0F).texture(u * uScale, (v + regionHeight) * vScale).next();
        bufferBuilder.vertex(matrix, x + regionWidth, y + regionHeight, 0.0F).texture((u + regionWidth) * uScale, (v + regionHeight) * vScale).next();
        bufferBuilder.vertex(matrix, x + regionWidth, y, 0.0F).texture((u + regionWidth) * uScale, v * vScale).next();
        bufferBuilder.vertex(matrix, x, y, 0.0F).texture(u * uScale, v * vScale).next();
        tessellator.draw();
    }

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            ordinal = 0
        ),
        method = "renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V"
    )
    private void renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
        PlayerEntity player = getCameraPlayer();
        HitResult hitResult = client.crosshairTarget;
        if (player != null && hitResult != null) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState state = client.world.getBlockState(pos);
                if (state.getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR && state.get(ClotheslineAnchorBlock.CRANK)) {
                    Vec3d hitVec = hitResult.getPos();
                    int offset = ClotheslineAnchorBlock.getCrankMultiplier(pos, hitVec.x, hitVec.z, player) * -8;
                    client.getTextureManager().bindTexture(CLOTHESLINE_GUI_ICONS);
                    drawTexture(matrices, scaledWidth / 2.0F - 7.5F + offset, scaledHeight / 2.0F - 7.5F, 8 + offset, 0.0F, 15, 15, CLOTHESLINE_ICONS_WIDTH, CLOTHESLINE_ICONS_HEIGHT);
                    client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
                }
            }
        }
    }
}
