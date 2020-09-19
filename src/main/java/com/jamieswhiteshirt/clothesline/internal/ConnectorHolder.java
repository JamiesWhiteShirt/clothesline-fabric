package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.item.ItemUsageContext;

import org.jetbrains.annotations.Nullable;

public interface ConnectorHolder {
    @Nullable
    ItemUsageContext clothesline$getFrom();

    void clothesline$setFrom(@Nullable ItemUsageContext context);
}
