package com.jamieswhiteshirt.clothesline.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

@Environment(EnvType.CLIENT)
public class BakedModels {
    private static final ModelIdentifier CRANK = new ModelIdentifier(new Identifier("clothesline", "crank"), "inventory");
    private static final ModelIdentifier PULLEY_WHEEL = new ModelIdentifier(new Identifier("clothesline", "pulley_wheel"), "inventory");
    private static final ModelIdentifier PULLEY_WHEEL_ROPE = new ModelIdentifier(new Identifier("clothesline", "pulley_wheel_rope"), "inventory");

    public static BakedModel crank;
    public static BakedModel pulleyWheel;
    public static BakedModel pulleyWheelRope;

    public static void init() {
        ModelLoadingRegistry.INSTANCE.registerAppender((manager, out) -> {
            out.accept(PULLEY_WHEEL);
            out.accept(PULLEY_WHEEL_ROPE);
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("clothesline", "models");
            }

            @Override
            public Collection<Identifier> getFabricDependencies() {
                return Collections.singleton(ResourceReloadListenerKeys.MODELS);
            }

            @Override
            public void reload(ResourceManager var1) {
                BakedModelManager bakedModelManager = MinecraftClient.getInstance().getBakedModelManager();
                crank = bakedModelManager.getModel(CRANK);
                pulleyWheel = bakedModelManager.getModel(PULLEY_WHEEL);
                pulleyWheelRope = bakedModelManager.getModel(PULLEY_WHEEL_ROPE);
            }
        });
    }
}
