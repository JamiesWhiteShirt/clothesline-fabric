package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.NetworkRaytraceHit;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.NetworkRaytraceHitEntity;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.Ray;
import com.jamieswhiteshirt.clotheslinefabric.client.raytrace.Raytracing;
import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import net.minecraft.client.MinecraftClient;
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
        HitResult hitResult = client.crosshairTarget;
        if (hitResult != null) {
            NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
            Vec3d rayFrom = cameraEntity.getCameraPosVec(delta);
            Vec3d rayTo = hitResult.getPos();

            Ray ray = new Ray(rayFrom, rayTo);

            NetworkRaytraceHit hit = Raytracing.raytraceNetworks(manager, ray, ray.lengthSq, delta);
            if (hit != null) {
                NetworkRaytraceHitEntity hitResultEntity = new NetworkRaytraceHitEntity(world, hit);
                client.crosshairTarget = new EntityHitResult(hitResultEntity);
                client.targetedEntity = hitResultEntity;
            }
        }
    }
}
