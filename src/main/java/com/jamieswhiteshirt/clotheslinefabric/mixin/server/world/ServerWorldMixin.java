package com.jamieswhiteshirt.clotheslinefabric.mixin.server.world;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkCollection;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.common.NetworkProviderPersistentState;
import com.jamieswhiteshirt.clotheslinefabric.common.impl.*;
import com.jamieswhiteshirt.clotheslinefabric.internal.NetworkCollectionTracker;
import com.jamieswhiteshirt.clotheslinefabric.internal.NetworkProvider;
import com.jamieswhiteshirt.clotheslinefabric.internal.WorldExtension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;
import java.util.function.BiFunction;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements NetworkManagerProvider, WorldExtension {
    private static final String PERSISTENT_STATE_KEY = "clothesline_provider";

    private final SetMultimap<ChunkPos, ServerPlayerEntity> chunkWatchers = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    private final NetworkCollection networkCollection = new NetworkCollectionImpl();
    private final NetworkProvider networkProvider = new NetworkProviderImpl(networkCollection, pos -> isChunkLoaded(ChunkPos.getPackedX(pos), ChunkPos.getPackedZ(pos)));
    private final NetworkManager networkManager = new ServerNetworkManager((ServerWorld)(Object) this, networkCollection, networkProvider);
    private final NetworkCollectionTracker<ServerPlayerEntity> tracker = new NetworkCollectionTrackerImpl<>(networkCollection, chunkWatchers::get, new PlayerNetworkMessenger());

    protected ServerWorldMixin(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> biFunction, Profiler profiler, boolean boolean_1) {
        super(levelProperties, dimensionType, biFunction, profiler, boolean_1);
    }

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/WorldSaveHandler;Lnet/minecraft/world/level/LevelProperties;Lnet/minecraft/world/dimension/DimensionType;Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/server/WorldGenerationProgressListener;)V"
    )
    private void constructor(MinecraftServer server, Executor executor, WorldSaveHandler oldWorldSaveHandler, LevelProperties levelProperties, DimensionType dimensionType, Profiler profiler, WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        PersistentStateManager persistentStateManager = ((ServerWorld) (Object) this).getPersistentStateManager();
        persistentStateManager.getOrCreate(() -> new NetworkProviderPersistentState(PERSISTENT_STATE_KEY, networkProvider), PERSISTENT_STATE_KEY);
    }

    @Override
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    @Override
    public void clotheslineTick() {
        networkManager.update();
        tracker.update();
    }

    @Override
    public void onPlayerWatchChunk(ChunkPos pos, ServerPlayerEntity player) {
        chunkWatchers.put(pos, player);
        tracker.onWatchChunk(player, pos);
    }

    @Override
    public void onPlayerUnWatchChunk(ChunkPos pos, ServerPlayerEntity player) {
        chunkWatchers.remove(pos, player);
        tracker.onUnWatchChunk(player, pos);
    }

    @Override
    public void onChunkLoaded(ChunkPos pos) {
        networkProvider.onChunkLoaded(pos);
    }

    @Override
    public void onChunkUnloaded(ChunkPos pos) {
        networkProvider.onChunkUnloaded(pos);
    }
}
