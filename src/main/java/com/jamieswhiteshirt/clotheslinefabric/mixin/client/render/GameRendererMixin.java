package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.NetworkRaytraceHitEntity;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.NetworkRaytraceHit;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.Ray;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.Raytracing;
import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private Camera camera;

    private final RenderClotheslineNetwork renderClotheslineNetwork = new RenderClotheslineNetwork(client);

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
        ),
        method = "updateTargetedEntity(F)V"
    )
    private void updateTargetedEntity(float delta, CallbackInfo ci) {
        World world = client.world;
        Entity cameraEntity = client.getCameraEntity();
        HitResult hitResult = client.hitResult;
        if (hitResult != null) {
            NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
            Vec3d rayFrom = cameraEntity.getCameraPosVec(delta);
            Vec3d rayTo = hitResult.getPos();

            Ray ray = new Ray(rayFrom, rayTo);

            NetworkRaytraceHit hit = Raytracing.raytraceNetworks(manager, ray, ray.lengthSq, delta);
            if (hit != null) {
                NetworkRaytraceHitEntity hitResultEntity = new NetworkRaytraceHitEntity(world, hit);
                client.hitResult = new EntityHitResult(hitResultEntity);
                client.targetedEntity = hitResultEntity;
            }
        }
    }

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;drawHighlightedBlockOutline(Lnet/minecraft/client/render/Camera;Lnet/minecraft/util/hit/HitResult;I)V"
        ),
        method = "renderCenter(FJ)V"
    )
    private void renderCenter(float delta, long long_1, CallbackInfo ci) {
        HitResult hitResult = client.hitResult;
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hitResult).getEntity() instanceof NetworkRaytraceHitEntity) {
            NetworkRaytraceHitEntity entity = (NetworkRaytraceHitEntity) ((EntityHitResult) hitResult).getEntity();

            double x = camera.getPos().x;
            double y = camera.getPos().y;
            double z = camera.getPos().z;
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5F, (float)this.client.window.getFramebufferWidth() / 1920.0F * 2.5F));
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            entity.getHit().renderHighlight(renderClotheslineNetwork, delta, x, y, z, 0.0F, 0.0F, 0.0F, 0.4F);

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
        }
    }
}
