package com.jamieswhiteshirt.clothesline.internal;

import com.jamieswhiteshirt.clothesline.api.Network;
import net.minecraft.item.ItemStack;

public interface NetworkMessenger<T> {
    void addNetwork(T watcher, Network network);

    void removeNetwork(T watcher, Network network);

    void setAttachment(T watcher, Network network, int attachmentKey, ItemStack stack);

    void setShiftAndMomentum(T watcher, Network network, int shift, int momentum);
}
