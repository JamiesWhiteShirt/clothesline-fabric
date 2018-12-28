package com.jamieswhiteshirt.clotheslinefabric.mixin.world.chunk;

import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkLoadEvent;
import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    @Shadow @Final private World world;
    @Shadow @Final private ChunkPos pos;

    @Inject(
        at = @At("RETURN"),
        method = "loadToWorld()V"
    )
    private void loadToWorld(CallbackInfo ci) {
        for (BiConsumer<World, ChunkPos> consumer : ((HandlerArray<BiConsumer<World, ChunkPos>>) ChunkLoadEvent.LOAD).getBackingArray()) {
            consumer.accept(world, pos);
        }
    }

    @Inject(
        at = @At("RETURN"),
        method = "unloadFromWorld()V"
    )
    private void unloadFromWorld(CallbackInfo ci) {
        for (BiConsumer<World, ChunkPos> consumer : ((HandlerArray<BiConsumer<World, ChunkPos>>) ChunkLoadEvent.UNLOAD).getBackingArray()) {
            consumer.accept(world, pos);
        }
    }
}
