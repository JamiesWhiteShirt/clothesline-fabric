package com.jamieswhiteshirt.clotheslinefabric.mixin.server.network;

import com.jamieswhiteshirt.clotheslinefabric.common.event.TrackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(
        at = @At("TAIL"),
        method = "onStartedTracking(Lnet/minecraft/entity/Entity;)V"
    )
    private void onStartedTracking(Entity entity, CallbackInfo ci) {
        TrackEntityCallback.START.invoker().accept((ServerPlayerEntity) (Object) this, entity);
    }
}
