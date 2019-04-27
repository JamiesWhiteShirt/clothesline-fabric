package com.jamieswhiteshirt.clotheslinefabric.common.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ClotheslineRecipeTypes {
    public static final RecipeType<SpinnerRecipe> SPINNER = register("spinner");

    private static <T extends Recipe<?>> RecipeType<T> register(final String name) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier("clothesline-fabric", name), new RecipeType<T>() {
            public String toString() {
                return name;
            }
        });
    }

    public static void init() { }
}
