package com.jamieswhiteshirt.clotheslinefabric.common.recipe;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SpinnerRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final String group;
    private final Ingredient input;
    private final ItemStack output;

    public SpinnerRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return input.method_8093(inventory.getInvStack(0));
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> defaultedList_1 = DefaultedList.create();
        defaultedList_1.add(this.input);
        return defaultedList_1;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return ClotheslineRecipeTypes.SPINNER;
    }

    public static class Serializer implements RecipeSerializer<SpinnerRecipe> {
        @Override
        public SpinnerRecipe read(Identifier id, JsonObject jsonObject) {
            String group = JsonHelper.getString(jsonObject, "group", "");
            Ingredient input;
            if (JsonHelper.hasArray(jsonObject, "ingredient")) {
                input = Ingredient.fromJson(JsonHelper.getArray(jsonObject, "ingredient"));
            } else {
                input = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
            }

            String resultItem = JsonHelper.getString(jsonObject, "result");
            int resultCount = JsonHelper.getInt(jsonObject, "count");
            ItemStack itemStack_1 = new ItemStack(Registry.ITEM.get(new Identifier(resultItem)), resultCount);
            return new SpinnerRecipe(id, group, input, itemStack_1);
        }

        @Override
        public SpinnerRecipe read(Identifier id, PacketByteBuf buf) {
            String group = buf.readString(32767);
            Ingredient input = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();
            return new SpinnerRecipe(id, group, input, output);
        }

        @Override
        public void write(PacketByteBuf buf, SpinnerRecipe recipe) {
            buf.writeString(recipe.getGroup(), 32767);
            recipe.input.write(buf);
            buf.writeItemStack(recipe.getOutput());
        }
    }
}
