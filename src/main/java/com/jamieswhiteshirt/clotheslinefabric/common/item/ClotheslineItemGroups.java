package com.jamieswhiteshirt.clotheslinefabric.common.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ClotheslineItemGroups {
    public static final ItemGroup ITEMS = FabricItemGroupBuilder.build(new Identifier("clothesline-fabric", "items"), () -> new ItemStack(ClotheslineItems.CLOTHESLINE));
}
