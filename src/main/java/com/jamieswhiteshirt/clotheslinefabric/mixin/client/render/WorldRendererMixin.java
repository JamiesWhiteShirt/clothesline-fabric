package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.NetworkRaytraceHitEntity;
import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_856;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow private ClientWorld world;
    private RenderClotheslineNetwork renderClotheslineNetwork = new RenderClotheslineNetwork(MinecraftClient.getInstance());

    @Inject(
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/Profiler;endBegin(Ljava/lang/String;)V",
            args = "ldc=blockentities"
        ),
        method = "renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/class_856;F)V"
    )
    private void renderEntities(Entity cameraEntity, class_856 camera, float delta, CallbackInfo ci) {
        world.getProfiler().endBegin("renderClotheslines");
        double x = MathHelper.lerp(delta, cameraEntity.prevRenderX, cameraEntity.x);
        double y = MathHelper.lerp(delta, cameraEntity.prevRenderY, cameraEntity.y);
        double z = MathHelper.lerp(delta, cameraEntity.prevRenderZ, cameraEntity.z);

        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        boolean showDebugInfo = client.options.debugEnabled;
        renderClotheslineNetwork.render(world, manager.getNetworks().getEdges(), camera, x, y, z, delta);
        if (showDebugInfo) {
            renderClotheslineNetwork.debugRender(manager.getNetworks().getNodes(), manager.getNetworks().getEdges(), camera, x, y, z, delta);
        }

        // If not third person
        if (client.options.field_1850 <= 0 && cameraEntity instanceof PlayerEntity) {
            renderClotheslineNetwork.renderFirstPersonPlayerHeldClothesline((PlayerEntity) cameraEntity, x, y, z, delta);
        }
    }

    @Inject(
        at = @At("TAIL"),
        method = "drawHighlightedBlockOutline(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/HitResult;IF)V"
    )
    private void method_3294(PlayerEntity player, HitResult hitResult, int var3, float delta, CallbackInfo ci) {
        if (var3 == 0 && hitResult.type == HitResult.Type.ENTITY && hitResult.entity instanceof NetworkRaytraceHitEntity) {
            NetworkRaytraceHitEntity entity = (NetworkRaytraceHitEntity) hitResult.entity;

            double x = MathHelper.lerp(delta, player.prevRenderX, player.x);
            double y = MathHelper.lerp(delta, player.prevRenderY, player.y);
            double z = MathHelper.lerp(delta, player.prevRenderZ, player.z);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5F, (float)this.client.window.getWindowWidth() / 1920.0F * 2.5F));
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            entity.getHit().renderHighlight(renderClotheslineNetwork, delta, x, y, z, 0.0F, 0.0F, 0.0F, 0.4F);

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
        }
    }
}
