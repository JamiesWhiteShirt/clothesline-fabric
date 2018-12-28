package com.jamieswhiteshirt.clotheslinefabric.client;

import com.jamieswhiteshirt.clotheslinefabric.api.*;
import com.jamieswhiteshirt.clotheslinefabric.client.audio.ClotheslineRopeSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.util.math.BlockPos;

import java.util.*;

@Environment(EnvType.CLIENT)
public class SoundNetworkCollectionListener implements NetworkCollectionListener {
    private final Map<BlockPos, ClotheslineRopeSoundInstance> anchorSounds = new HashMap<>();
    private final SoundLoader soundHandler = MinecraftClient.getInstance().getSoundLoader();

    private void listenTo(NetworkState state) {
        for (Path.Node node : state.getPath().getNodes().values()) {
            ClotheslineRopeSoundInstance sound = new ClotheslineRopeSoundInstance(state, node);
            anchorSounds.put(node.getPos(), sound);
            soundHandler.play(sound);
        }
    }

    private void unlistenTo(NetworkState state) {
        for (BlockPos pos : state.getPath().getNodes().keySet()) {
            ClotheslineRopeSoundInstance sound = anchorSounds.remove(pos);
            if (sound != null) {
                soundHandler.stop(sound);
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
