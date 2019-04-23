package com.jamieswhiteshirt.clotheslinefabric.mixin.server.network;

import com.jamieswhiteshirt.clotheslinefabric.common.event.TrackEntityCallback;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world_1, GameProfile gameProfile_1) {
        super(world_1, gameProfile_1);
    }

    @Inject(
        at = @At("TAIL"),
        method = "onStartedTracking(Lnet/minecraft/entity/Entity;)V"
    )
    private void onStartedTracking(Entity entity, CallbackInfo ci) {
        TrackEntityCallback.START.invoker().accept((ServerPlayerEntity) (Object) this, entity);
    }
}
