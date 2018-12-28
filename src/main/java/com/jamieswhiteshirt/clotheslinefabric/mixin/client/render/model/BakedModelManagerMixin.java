package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render.model;

import com.jamieswhiteshirt.clotheslinefabric.client.event.BakedModelManagerEvent;
import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {
    @Inject(
        at = @At("TAIL"),
        method = "onResourceReload(Lnet/minecraft/resource/ResourceManager;)V"
    )
    private void onResourceReload(ResourceManager resourceManager_1, CallbackInfo ci) {
        for (Consumer<BakedModelManager> consumer : ((HandlerArray<Consumer<BakedModelManager>>) BakedModelManagerEvent.GET_MODELS).getBackingArray()) {
            consumer.accept((BakedModelManager) (Object) this);
        }
    }
}
