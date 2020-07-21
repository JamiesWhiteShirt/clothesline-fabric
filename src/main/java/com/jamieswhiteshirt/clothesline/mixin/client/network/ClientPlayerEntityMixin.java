package com.jamieswhiteshirt.clothesline.mixin.client.network;

import com.jamieswhiteshirt.clothesline.common.item.ConnectorItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld clientWorld_1, GameProfile gameProfile_1) {
        super(clientWorld_1, gameProfile_1);
    }

    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
        ),
        method = "tickMovement()V"
    )
    private boolean redirectIsUsingItem(ClientPlayerEntity clientPlayerEntity) {
        return clientPlayerEntity.isUsingItem() && !(clientPlayerEntity.getActiveItem().getItem() instanceof ConnectorItem);
    }
}
