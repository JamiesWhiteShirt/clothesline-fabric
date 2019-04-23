package com.jamieswhiteshirt.clotheslinefabric.mixin.client.network;

import com.jamieswhiteshirt.clotheslinefabric.common.item.ConnectorItem;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
        ),
        method = "updateState()V"
    )
    private boolean redirectIsUsingItem(ClientPlayerEntity clientPlayerEntity) {
        return clientPlayerEntity.isUsingItem() && !(clientPlayerEntity.getActiveItem().getItem() instanceof ConnectorItem);
    }
}
