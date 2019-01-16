package com.jamieswhiteshirt.clotheslinefabric.client;

import com.jamieswhiteshirt.clotheslinefabric.client.network.ClientMessageHandling;
import com.jamieswhiteshirt.clotheslinefabric.client.render.BakedModels;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClotheslineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientMessageHandling.init();
        BakedModels.init();
    }
}
