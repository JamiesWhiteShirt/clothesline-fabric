package com.jamieswhiteshirt.clotheslinefabric.mixin.client.render.model;

import com.jamieswhiteshirt.clotheslinefabric.client.event.BakedModelManagerCallback;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {
    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
        ),
        method = "method_18179(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/util/profiler/Profiler;)V"
    )
    private void onResourceReload(ResourceManager resourceManager_1, ModelLoader modelLoader_1, Profiler profiler_1, CallbackInfo ci) {
        BakedModelManagerCallback.GET_MODELS.invoker().accept((BakedModelManager) (Object) this);
    }
}
