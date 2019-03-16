package com.jamieswhiteshirt.clotheslinefabric.common.item;

import com.jamieswhiteshirt.clotheslinefabric.internal.ItemExtension;
import net.minecraft.item.Item;

public class SpinnerItem extends Item {
    public SpinnerItem(Settings settings) {
        super(settings);
        ((ItemExtension) this).clothesline_setRecipeRemainder(this);
    }
}
