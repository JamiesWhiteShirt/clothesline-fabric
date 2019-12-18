package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render.entity;

import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
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
        method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
    )
    private void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int overlay, CallbackInfo ci) {
        if (entity.getActiveItem().getItem() != ClotheslineItems.CLOTHESLINE) return;

        // TODO: How to deal with this
        /* double posX = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        double posY = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        double posZ = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()); */

        renderClotheslineNetwork.renderThirdPersonPlayerHeldClothesline(entity, 0.0D, 0.0D, 0.0D, tickDelta);
    }
}
