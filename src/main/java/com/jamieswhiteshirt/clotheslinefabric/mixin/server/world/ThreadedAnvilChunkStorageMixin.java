package com.jamieswhiteshirt.clotheslinefabric.mixin.server.world;

import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkLoadCallback;
import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkWatchCallback;
import com.mojang.datafixers.DataFixer;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.VersionedChunkStorage;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin extends VersionedChunkStorage {
    @Shadow @Final private ServerWorld world;

    public ThreadedAnvilChunkStorageMixin(File file_1, DataFixer dataFixer_1) {
        super(file_1, dataFixer_1);
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

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/WorldChunk;setLoadedToWorld(Z)V",
            shift = At.Shift.AFTER
        ),
        method = "method_17227(Lnet/minecraft/server/world/ChunkHolder;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/world/chunk/Chunk;"
    )
    private void method_17227(ChunkHolder holder, Chunk chunk, CallbackInfoReturnable<Chunk> ci) {
        ChunkLoadCallback.LOAD.invoker().accept(world, holder.getPos());
    }

    @Inject(
        at = @At(
            value = "RETURN",
            target = "Lnet/minecraft/world/chunk/WorldChunk;setLoadedToWorld(Z)V",
            shift = At.Shift.AFTER
        ),
        method = "method_18843(Lnet/minecraft/server/world/ChunkHolder;Ljava/util/concurrent/CompletableFuture;JLnet/minecraft/world/chunk/Chunk;)V"
    )
    private void method_18843(ChunkHolder chunkHolder, CompletableFuture future, long pos, Chunk chunk, CallbackInfo ci) {
        ChunkLoadCallback.UNLOAD.invoker().accept(world, chunkHolder.getPos());
    }
}
