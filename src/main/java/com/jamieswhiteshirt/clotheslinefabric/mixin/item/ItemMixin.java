package com.jamieswhiteshirt.clotheslinefabric.mixin.item;

import com.jamieswhiteshirt.clotheslinefabric.internal.ItemExtension;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public class ItemMixin implements ItemExtension {
    @Mutable @Shadow @Final private Item recipeRemainder;

    @Override
    public void clothesline_setRecipeRemainder(Item item) {
        recipeRemainder = item;
    }
}
