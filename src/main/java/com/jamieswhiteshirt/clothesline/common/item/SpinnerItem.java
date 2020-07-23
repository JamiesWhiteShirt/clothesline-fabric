package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.mixin.item.ItemAccessor;
import net.minecraft.item.Item;

public class SpinnerItem extends Item {
    public SpinnerItem(Settings settings) {
        super(settings);
        ((ItemAccessor) this).clothesline$setRecipeRemainder(this);
    }
}
