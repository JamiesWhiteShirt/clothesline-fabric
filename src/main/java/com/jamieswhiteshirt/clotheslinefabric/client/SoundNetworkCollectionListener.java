package com.jamieswhiteshirt.clotheslinefabric.client;

import com.jamieswhiteshirt.clotheslinefabric.api.*;
import com.jamieswhiteshirt.clotheslinefabric.client.audio.ClotheslineRopeSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.util.math.BlockPos;

import java.util.*;

@Environment(EnvType.CLIENT)
public class SoundNetworkCollectionListener implements NetworkCollectionListener {
    private final Map<BlockPos, SoundInstance> anchorSoundInstances = new HashMap<>();
    private final SoundLoader soundHandler = MinecraftClient.getInstance().getSoundLoader();

    private void listenTo(NetworkState state) {
        for (Path.Node node : state.getPath().getNodes().values()) {
            SoundInstance soundInstance = new ClotheslineRopeSoundInstance(state, node);
            anchorSoundInstances.put(node.getPos(), soundInstance);
            soundHandler.play(soundInstance);
        }
    }

    private void unlistenTo(NetworkState state) {
        for (BlockPos pos : state.getPath().getNodes().keySet()) {
            SoundInstance soundInstance = anchorSoundInstances.remove(pos);
            if (soundInstance != null) {
                soundHandler.stop(soundInstance);
            }
        }
    }

    @Override
    public void onNetworkAdded(NetworkCollection networks, Network network) {
        listenTo(network.getState());
    }

    @Override
    public void onNetworkRemoved(NetworkCollection networks, Network network) {
        unlistenTo(network.getState());
    }
}
