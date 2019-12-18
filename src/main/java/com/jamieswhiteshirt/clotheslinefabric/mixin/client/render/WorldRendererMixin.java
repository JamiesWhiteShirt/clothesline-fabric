package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.NetworkRaytraceHitEntity;
import com.jamieswhiteshirt.clotheslinefabric.client.render.ClotheslineRenderLayers;
import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
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
    private final RenderClotheslineNetwork renderClotheslineNetwork = new RenderClotheslineNetwork(MinecraftClient.getInstance());

    @Inject(
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=blockentities"
        ),
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/util/math/Matrix4f;)V"
    )
    private void renderClotheslines(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci, VertexConsumerProvider.Immediate immediate, Profiler profiler, HitResult hitResult) {
        world.getProfiler().swap("clotheslines");
        double x = camera.getPos().x;
        double y = camera.getPos().y;
        double z = camera.getPos().z;

        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        boolean showDebugInfo = client.options.debugEnabled;
        renderClotheslineNetwork.render(matrices, immediate, world, manager.getNetworks().getNodes(), manager.getNetworks().getEdges(), visibleRegion, x, y, z, tickDelta);
        if (showDebugInfo) {
            renderClotheslineNetwork.debugRender(matrices, immediate, manager.getNetworks().getNodes(), manager.getNetworks().getEdges(), visibleRegion, x, y, z, tickDelta);
        }

        // If not third person
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        if (client.options.perspective <= 0 && entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            if (playerEntity.getActiveItem().getItem() == ClotheslineItems.CLOTHESLINE) {
                renderClotheslineNetwork.renderFirstPersonPlayerHeldClothesline(matrices, immediate, (PlayerEntity) entity, x, y, z, tickDelta);
            }
        }
        immediate.draw(ClotheslineRenderLayers.getClothesline());
    }

    @Inject(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
            shift = At.Shift.AFTER,
            ordinal = 1
        ),
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/util/math/Matrix4f;)V"
    )
    private void renderHighlight(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci, VertexConsumerProvider.Immediate immediate, Profiler profiler, HitResult hitResult) {
        if (renderBlockOutline && hitResult != null && hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hitResult).getEntity() instanceof NetworkRaytraceHitEntity) {
            profiler.swap("outline");
            NetworkRaytraceHitEntity entity = (NetworkRaytraceHitEntity) ((EntityHitResult) hitResult).getEntity();
            VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines());
            Vec3d vec3d = camera.getPos();
            double x = vec3d.getX();
            double y = vec3d.getY();
            double z = vec3d.getZ();
            entity.getHit().renderHighlight(renderClotheslineNetwork, matrices, vertexConsumer, tickDelta, x, y, z, 0.0F, 0.0F, 0.0F, 0.4F);
        }
    }
}
