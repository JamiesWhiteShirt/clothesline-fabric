package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
        method = "renderEntities(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/VisibleRegion;F)V"
    )
    private void renderEntities(Camera camera, VisibleRegion visibleRegion, float delta, CallbackInfo ci) {
        world.getProfiler().swap("renderClotheslines");
        double x = camera.getPos().x;
        double y = camera.getPos().y;
        double z = camera.getPos().z;

        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        boolean showDebugInfo = client.options.debugEnabled;
        renderClotheslineNetwork.render(world, manager.getNetworks().getNodes(), manager.getNetworks().getEdges(), visibleRegion, x, y, z, delta);
        if (showDebugInfo) {
            renderClotheslineNetwork.debugRender(manager.getNetworks().getNodes(), manager.getNetworks().getEdges(), visibleRegion, x, y, z, delta);
        }

        // If not third person
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        if (client.options.perspective <= 0 && entity instanceof PlayerEntity) {
            renderClotheslineNetwork.renderFirstPersonPlayerHeldClothesline((PlayerEntity) entity, x, y, z, delta);
        }
    }
}
