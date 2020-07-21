package com.jamieswhiteshirt.clothesline.mixin.server.world;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.jamieswhiteshirt.clothesline.api.NetworkCollection;
import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.common.NetworkProviderPersistentState;
import com.jamieswhiteshirt.clothesline.common.impl.*;
import com.jamieswhiteshirt.clothesline.internal.NetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.internal.NetworkProvider;
import com.jamieswhiteshirt.clothesline.internal.WorldExtension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements NetworkManagerProvider, WorldExtension {
    private static final String PERSISTENT_STATE_KEY = "clothesline_provider";

    private final SetMultimap<ChunkPos, ServerPlayerEntity> chunkWatchers = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    private final NetworkCollection networkCollection = new NetworkCollectionImpl();
    private final NetworkProvider networkProvider = new NetworkProviderImpl(networkCollection, pos -> isChunkLoaded(ChunkPos.getPackedX(pos), ChunkPos.getPackedZ(pos)));
    private final NetworkManager networkManager = new ServerNetworkManager((ServerWorld)(Object) this, networkCollection, networkProvider);
    private final NetworkCollectionTracker<ServerPlayerEntity> tracker = new NetworkCollectionTrackerImpl<>(networkCollection, chunkWatchers::get, new PlayerNetworkMessenger());

    protected ServerWorldMixin(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, Supplier<Profiler> profiler, boolean bl, boolean bl2, long l) {
        super(mutableWorldProperties, registryKey, registryKey2, dimensionType, profiler, bl, bl2, l);
    }

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/level/ServerWorldProperties;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionType;Lnet/minecraft/server/WorldGenerationProgressListener;Lnet/minecraft/world/gen/chunk/ChunkGenerator;ZJLjava/util/List;Z)V"
    )
    private void constructor(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, WorldGenerationProgressListener generationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<Spawner> list, boolean bl2, CallbackInfo ci) {
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
