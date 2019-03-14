package com.jamieswhiteshirt.clotheslinefabric.mixin.client.gui.hud;

import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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
    private static final Identifier CLOTHESLINE_ICONS = new Identifier("clothesline-fabric", "textures/gui/icons.png");
    private static final int CLOTHESLINE_ICONS_WIDTH = 32, CLOTHESLINE_ICONS_HEIGHT = 16;

    private static void drawTexturedRect(float x, float y, float uMin, float vMin, int width, int height, float vSize, float uSize) {
        float vScale = 1.0F / vSize;
        float uScale = 1.0F / uSize;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
        bufferBuilder.vertex((double)x, (double)(y + height), 0.0D).texture((double)(uMin * vScale), (double)((vMin + (float)height) * uScale)).next();
        bufferBuilder.vertex((double)(x + width), (double)(y + height), 0.0D).texture((double)((uMin + (float)width) * vScale), (double)((vMin + (float)height) * uScale)).next();
        bufferBuilder.vertex((double)(x + width), (double)y, 0.0D).texture((double)((uMin + (float)width) * vScale), (double)(vMin * uScale)).next();
        bufferBuilder.vertex((double)x, (double)y, 0.0D).texture((double)(uMin * vScale), (double)(vMin * uScale)).next();
        tessellator.draw();
    }

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexturedRect(FFIIII)V"
        ),
        method = "renderCrosshair()V"
    )
    private void renderCrosshair(CallbackInfo ci) {
        PlayerEntity player = getCameraPlayer();
        HitResult hitResult = client.hitResult;
        if (player != null && hitResult != null) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState state = client.world.getBlockState(pos);
                if (state.getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR && state.get(ClotheslineAnchorBlock.CRANK)) {
                    Vec3d hitVec = hitResult.getPos();
                    int offset = ClotheslineAnchorBlock.getCrankMultiplier(pos, hitVec.x, hitVec.z, player) * -8;
                    client.getTextureManager().bindTexture(CLOTHESLINE_ICONS);
                    drawTexturedRect(scaledWidth / 2.0F - 7.5F + offset, scaledHeight / 2.0F - 7.5F, 8 + offset, 0.0F, 15, 15, CLOTHESLINE_ICONS_WIDTH, CLOTHESLINE_ICONS_HEIGHT);
                    client.getTextureManager().bindTexture(ICONS);
                }
            }
        }
    }
}
