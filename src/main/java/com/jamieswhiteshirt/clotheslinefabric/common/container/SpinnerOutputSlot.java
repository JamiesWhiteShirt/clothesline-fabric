package com.jamieswhiteshirt.clotheslinefabric.common.container;

import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class SpinnerOutputSlot extends Slot {
    public SpinnerOutputSlot(Inventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }
}
