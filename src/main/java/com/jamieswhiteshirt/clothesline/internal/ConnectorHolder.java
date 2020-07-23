package com.jamieswhiteshirt.clothesline.internal;

import net.minecraft.item.ItemUsageContext;

import javax.annotation.Nullable;

public interface ConnectorHolder {
    @Nullable
    ItemUsageContext clothesline$getFrom();

    void clothesline$setFrom(@Nullable ItemUsageContext context);
}
