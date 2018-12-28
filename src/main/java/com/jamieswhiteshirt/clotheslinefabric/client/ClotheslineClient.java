package com.jamieswhiteshirt.clotheslinefabric.client;

import com.jamieswhiteshirt.clotheslinefabric.client.network.ClientMessageHandling;
import com.jamieswhiteshirt.clotheslinefabric.client.render.BakedModels;
import com.jamieswhiteshirt.clotheslinefabric.client.render.blockentity.ClotheslineAnchorBlockEntityRenderer;
import com.jamieswhiteshirt.clotheslinefabric.common.blockentity.ClotheslineAnchorBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ClotheslineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientMessageHandling.init();
        BlockEntityRendererRegistry.INSTANCE.register(ClotheslineAnchorBlockEntity.class, new ClotheslineAnchorBlockEntityRenderer());
        BakedModels.init();
    }
}
