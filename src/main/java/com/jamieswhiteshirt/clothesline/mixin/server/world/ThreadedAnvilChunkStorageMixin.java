package com.jamieswhiteshirt.clothesline.mixin.server.world;

import com.jamieswhiteshirt.clothesline.common.event.ChunkWatchCallback;
import com.mojang.datafixers.DataFixer;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin extends VersionedChunkStorage {
    @Shadow @Final private ServerWorld world;

    public ThreadedAnvilChunkStorageMixin(File file, DataFixer dataFixer, boolean bl) {
        super(file, dataFixer, bl);
    }

    @Inject(
        at = @At("RETURN"),
        method = "sendWatchPackets(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/ChunkPos;[Lnet/minecraft/network/Packet;ZZ)V"
    )
    private void sendWatchPackets(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean previouslyWatching, boolean currentlyWatching, CallbackInfo ci) {
        if (player.world == world) {
            if (currentlyWatching && !previouslyWatching) {
                ChunkWatchCallback.WATCH.invoker().accept(world, pos, player);
            }
            if (!currentlyWatching && previouslyWatching) {
                ChunkWatchCallback.UNWATCH.invoker().accept(world, pos, player);
            }
        }
    }
}
