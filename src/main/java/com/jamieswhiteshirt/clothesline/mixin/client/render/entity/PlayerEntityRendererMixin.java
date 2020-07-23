package com.jamieswhiteshirt.clothesline.mixin.client.render.entity;

import com.jamieswhiteshirt.clothesline.client.render.ClotheslineRenderer;
import com.jamieswhiteshirt.clothesline.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.internal.ConnectorHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemUsageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final ClotheslineRenderer clotheslineRenderer = new ClotheslineRenderer(MinecraftClient.getInstance());

    public PlayerEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher_1, PlayerEntityModel<AbstractClientPlayerEntity> entityModel_1, float float_1) {
        super(entityRenderDispatcher_1, entityModel_1, float_1);
    }

    @Inject(
        at = @At("RETURN"),
        method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
    )
    private void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay, CallbackInfo ci) {
        if (entity.getActiveItem().getItem() != ClotheslineItems.CLOTHESLINE) return;
        ConnectorHolder connector = (ConnectorHolder) entity;
        ItemUsageContext from = connector.clothesline$getFrom();
        if (from == null) return;

        clotheslineRenderer.renderThirdPersonPlayerHeldClothesline(matrices, vertexConsumers, entity, from.getBlockPos(), tickDelta);
    }
}
