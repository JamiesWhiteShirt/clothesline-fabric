package com.jamieswhiteshirt.clotheslinefabric.common.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ClotheslineRecipeSerializers {
    public static final RecipeSerializer<SpinnerRecipe> SPINNER = register("spinner", new SpinnerRecipe.Serializer());

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier("clothesline-fabric", name), serializer);
    }

    public static void init() { }
}
