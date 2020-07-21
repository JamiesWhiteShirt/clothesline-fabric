package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.client.network.ClientMessageHandling;
import com.jamieswhiteshirt.clothesline.client.render.BakedModels;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class ClotheslineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientMessageHandling.init();
        BakedModels.init();

        BlockRenderLayerMap.INSTANCE.putBlock(ClotheslineBlocks.CLOTHESLINE_ANCHOR, RenderLayer.getCutout());
    }
}
