package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.api.client.RichInteractionRegistry;
import com.jamieswhiteshirt.clothesline.client.impl.RichInteractionRegistryImpl;
import com.jamieswhiteshirt.clothesline.client.network.ClientMessageHandling;
import com.jamieswhiteshirt.clothesline.client.render.BakedModels;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineBlocks;
import com.jamieswhiteshirt.clothesline.internal.WorldExtension;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class ClotheslineClient implements ClientModInitializer {
    public static RichInteractionRegistry richInteractionRegistry = new RichInteractionRegistryImpl();

    @Override
    public void onInitializeClient() {
        ClientMessageHandling.init();
        BakedModels.init();

        ClientTickEvents.END_WORLD_TICK.register(world -> ((WorldExtension) world).clothesline$tick());

        BlockRenderLayerMap.INSTANCE.putBlock(ClotheslineBlocks.CLOTHESLINE_ANCHOR, RenderLayer.getCutout());

        FabricLoader.getInstance()
            .getEntrypoints("clothesline:rich_interaction", RichInteractionRegistry.Consumer.class)
            .forEach(consumer -> consumer.accept(richInteractionRegistry));
    }
}
