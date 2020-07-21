package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.client.audio.ClotheslineRopeSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SoundNetworkCollectionListener implements NetworkCollectionListener {
    private final Map<BlockPos, SoundInstance> anchorSoundInstances = new HashMap<>();
    private final SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

    private void listenTo(NetworkState state) {
        for (Path.Node node : state.getPath().getNodes().values()) {
            SoundInstance soundInstance = new ClotheslineRopeSoundInstance(state, node);
            anchorSoundInstances.put(node.getPos(), soundInstance);
            soundManager.play(soundInstance);
        }
    }

    private void unlistenTo(NetworkState state) {
        for (BlockPos pos : state.getPath().getNodes().keySet()) {
            SoundInstance soundInstance = anchorSoundInstances.remove(pos);
            if (soundInstance != null) {
                soundManager.stop(soundInstance);
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
