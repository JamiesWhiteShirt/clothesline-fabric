package com.jamieswhiteshirt.clotheslinefabric.common.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ClotheslineBlockEntityTypes {
    public static final BlockEntityType<SpinnerBlockEntity> SPINNER = create("spinner", BlockEntityType.Builder.create(SpinnerBlockEntity::new));

    private static <T extends BlockEntity> BlockEntityType<T> create(String name, BlockEntityType.Builder<T> builder) {
        return Registry.register(Registry.BLOCK_ENTITY, new Identifier("clothesline-fabric", name), builder.build(null));
    }

    public static void init() { }
}
