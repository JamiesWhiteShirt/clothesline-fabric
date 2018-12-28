package com.jamieswhiteshirt.clotheslinefabric.common.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ClotheslineBlockEntityTypes {
    public static final BlockEntityType<ClotheslineAnchorBlockEntity> CLOTHESLINE_ANCHOR = create("clothesline_anchor", BlockEntityType.Builder.create(ClotheslineAnchorBlockEntity::new));

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(Registry.BLOCK_ENTITY, new Identifier("clothesline-fabric", id), builder.method_11034(null));
    }

    public static void init() {
    }
}
