package com.jamieswhiteshirt.clothesline.mixin.client.world;

import com.jamieswhiteshirt.clothesline.api.NetworkCollection;
import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.client.SoundNetworkCollectionListener;
import com.jamieswhiteshirt.clothesline.client.impl.ClientNetworkManager;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollectionImpl;
import com.jamieswhiteshirt.clothesline.internal.WorldExtension;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements NetworkManagerProvider, WorldExtension {
    private static final Identifier SOUND_KEY = new Identifier("clothesline-fabric", "sound");

    private final NetworkCollection networkCollection = new NetworkCollectionImpl();
    private final NetworkManager networkManager = new ClientNetworkManager((ClientWorld)(Object) this, networkCollection);

    protected ClientWorldMixin(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, Supplier<Profiler> profiler, boolean bl, boolean bl2, long l) {
        super(mutableWorldProperties, registryKey, registryKey2, dimensionType, profiler, bl, bl2, l);
    }

    @Inject(
        at = @At("RETURN"),
        method = "<init>(Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/client/world/ClientWorld$Properties;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionType;ILjava/util/function/Supplier;Lnet/minecraft/client/render/WorldRenderer;ZJ)V"
    )
    private void constructor(ClientPlayNetworkHandler clientPlayNetworkHandler, ClientWorld.Properties properties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, int i, Supplier<Profiler> supplier, WorldRenderer worldRenderer, boolean bl, long l, CallbackInfo ci) {
        networkManager.getNetworks().addEventListener(SOUND_KEY, new SoundNetworkCollectionListener());
    }

    @Override
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    @Override
    public void clotheslineTick() {
        networkManager.update();
    }

    @Override
    public void onPlayerWatchChunk(ChunkPos pos, ServerPlayerEntity player) {
    }

    @Override
    public void onPlayerUnWatchChunk(ChunkPos pos, ServerPlayerEntity player) {
    }

    @Override
    public void onChunkLoaded(ChunkPos pos) {
    }

    @Override
    public void onChunkUnloaded(ChunkPos pos) {
    }
}
