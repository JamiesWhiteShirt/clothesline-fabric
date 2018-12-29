package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render.entity;

import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final RenderClotheslineNetwork renderClotheslineNetwork = new RenderClotheslineNetwork(MinecraftClient.getInstance());

    public PlayerEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher_1, PlayerEntityModel<AbstractClientPlayerEntity> entityModel_1, float float_1) {
        super(entityRenderDispatcher_1, entityModel_1, float_1);
    }

    @Inject(
        at = @At("RETURN"),
        method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDFF)V"
    )
    private void method_4215(AbstractClientPlayerEntity player, double x, double y, double z, float float_1, float delta, CallbackInfo ci) {
        double posX = MathHelper.lerp(delta, player.prevRenderX, player.x);
        double posY = MathHelper.lerp(delta, player.prevRenderY, player.y);
        double posZ = MathHelper.lerp(delta, player.prevRenderZ, player.z);
        renderClotheslineNetwork.renderThirdPersonPlayerHeldClothesline(player, x + posX, y + posY, z + posZ, delta);
    }
}
