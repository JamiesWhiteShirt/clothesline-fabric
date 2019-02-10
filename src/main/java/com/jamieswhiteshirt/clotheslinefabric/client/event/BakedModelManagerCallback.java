package com.jamieswhiteshirt.clotheslinefabric.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.model.BakedModelManager;

@Environment(EnvType.CLIENT)
public interface BakedModelManagerCallback {
    Event<BakedModelManagerCallback> GET_MODELS = EventFactory.createArrayBacked(BakedModelManagerCallback.class, (listeners) -> (manager) -> {
        for (BakedModelManagerCallback callback : listeners) {
            callback.accept(manager);
        }
    });

    void accept(BakedModelManager manager);
}
