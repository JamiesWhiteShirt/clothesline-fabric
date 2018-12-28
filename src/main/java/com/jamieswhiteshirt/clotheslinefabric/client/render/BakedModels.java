package com.jamieswhiteshirt.clotheslinefabric.client.render;

import com.jamieswhiteshirt.clotheslinefabric.client.event.BakedModelManagerEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BakedModels {
    private static final ModelIdentifier PULLEY_WHEEL = new ModelIdentifier(new Identifier("clothesline-fabric", "pulley_wheel"), "inventory");
    private static final ModelIdentifier PULLEY_WHEEL_ROPE = new ModelIdentifier(new Identifier("clothesline-fabric", "pulley_wheel_rope"), "inventory");

    public static BakedModel pulleyWheel;
    public static BakedModel pulleyWheelRope;

    public static void init() {
        ModelLoadingRegistry.INSTANCE.registerAppender((manager, out) -> {
            out.accept(PULLEY_WHEEL);
            out.accept(PULLEY_WHEEL_ROPE);
        });

        BakedModelManagerEvent.GET_MODELS.register(bakedModelManager -> {
            pulleyWheel = bakedModelManager.getModel(PULLEY_WHEEL);
            pulleyWheelRope = bakedModelManager.getModel(PULLEY_WHEEL_ROPE);
        });
    }
}
