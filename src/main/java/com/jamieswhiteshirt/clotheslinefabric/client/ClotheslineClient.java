package com.jamieswhiteshirt.clotheslinefabric.client;

import com.jamieswhiteshirt.clotheslinefabric.client.network.ClientMessageHandling;
import com.jamieswhiteshirt.clotheslinefabric.client.render.BakedModels;
import com.jamieswhiteshirt.clotheslinefabric.client.render.block.entity.ClotheslineAnchorBlockEntityRenderer;
import com.jamieswhiteshirt.clotheslinefabric.common.block.entity.ClotheslineAnchorBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class ClotheslineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientMessageHandling.init();
        BlockEntityRendererRegistry.INSTANCE.register(ClotheslineAnchorBlockEntity.class, new ClotheslineAnchorBlockEntityRenderer());
        BakedModels.init();
    }
}
