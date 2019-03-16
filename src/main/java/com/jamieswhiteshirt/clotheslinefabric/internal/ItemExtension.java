package com.jamieswhiteshirt.clotheslinefabric.internal;

import net.minecraft.item.Item;

public interface ItemExtension {
    /**
     * "Why does this interface exist? Why aren't you using the recipe remainder in Item.Settings?"
     *
     * There is currently no convenient way to make an Item its own recipe remainder. This is caused by a circular
     * dependency - the Item requires the Item.Settings which requires the Item which requires the Item.Settings...
     *
     * This method is reserved for that case only.
     */
    void clothesline_setRecipeRemainder(Item item);
}
