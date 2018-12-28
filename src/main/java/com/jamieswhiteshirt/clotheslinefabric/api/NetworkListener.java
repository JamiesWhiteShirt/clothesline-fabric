package com.jamieswhiteshirt.clotheslinefabric.api;

import net.minecraft.item.ItemStack;

public interface NetworkListener {
    void onAttachmentChanged(Network network, int attachmentKey, ItemStack previousStack, ItemStack newStack);
}
