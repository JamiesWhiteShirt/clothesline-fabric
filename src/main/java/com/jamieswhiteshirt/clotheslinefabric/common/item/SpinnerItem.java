package com.jamieswhiteshirt.clotheslinefabric.common.item;

import com.jamieswhiteshirt.clotheslinefabric.mixin.item.ItemAccessor;
import net.minecraft.item.Item;

public class SpinnerItem extends Item {
    public SpinnerItem(Settings settings) {
        super(settings);
        ((ItemAccessor) this).setRecipeRemainder(this);
    }
}
