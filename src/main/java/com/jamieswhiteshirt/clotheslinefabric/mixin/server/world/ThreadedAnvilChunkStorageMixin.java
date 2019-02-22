package com.jamieswhiteshirt.clotheslinefabric.mixin.server.world;

import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkLoadCallback;
import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkWatchCallback;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
    @Shadow @Final private ServerWorld world;

    @Inject(
        at = @At("RETURN"),
        method = "sendWatchPackets(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/chunk/ChunkPos;[Lnet/minecraft/network/Packet;ZZ)V"
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

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/WorldChunk;loadToWorld()V",
            shift = At.Shift.AFTER
        ),
        method = "method_17227(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/server/world/ChunkHolder;)Lnet/minecraft/world/chunk/Chunk;"
    )
    private void method_17227(Chunk chunk, ChunkHolder chunkHolder, CallbackInfoReturnable<Chunk> ci) {
        ChunkLoadCallback.LOAD.invoker().accept(world, chunk.getPos());
    }

    @Inject(
        at = @At("RETURN"),
        method = "method_18708(Lnet/minecraft/world/chunk/WorldChunk;)V"
    )
    private void method_18708(WorldChunk chunk, CallbackInfo ci) {
        ChunkLoadCallback.UNLOAD.invoker().accept(world, chunk.getPos());
    }
}
