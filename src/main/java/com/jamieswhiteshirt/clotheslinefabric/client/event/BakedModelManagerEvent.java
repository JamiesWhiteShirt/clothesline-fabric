package com.jamieswhiteshirt.clotheslinefabric.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.util.HandlerArray;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.client.render.model.BakedModelManager;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class BakedModelManagerEvent {
    public static final HandlerRegistry<Consumer<BakedModelManager>> GET_MODELS = new HandlerArray<>(Consumer.class);

    private BakedModelManagerEvent() { }
}
