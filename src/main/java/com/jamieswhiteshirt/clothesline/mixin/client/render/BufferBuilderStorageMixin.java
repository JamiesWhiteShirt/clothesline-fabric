package com.jamieswhiteshirt.clothesline.mixin.client.render;

import com.jamieswhiteshirt.clothesline.client.render.ClotheslineRenderLayers;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilderStorage.class)
public abstract class BufferBuilderStorageMixin {
    @Shadow private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> builderStorage, RenderLayer layer) {}

    @Inject(
        at = @At("TAIL"),
        method = "method_22999(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;)V"
    )
    private void method_22999(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> layerBuffers, CallbackInfo ci) {
        assignBufferBuilder(layerBuffers, ClotheslineRenderLayers.getClothesline());
    }
}
