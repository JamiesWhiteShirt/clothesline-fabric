package com.jamieswhiteshirt.clotheslinefabric.internal;

import net.minecraft.item.ItemUsageContext;

import javax.annotation.Nullable;

public interface ConnectorHolder {
    @Nullable
    ItemUsageContext getFrom();

    void setFrom(@Nullable ItemUsageContext context);
}
