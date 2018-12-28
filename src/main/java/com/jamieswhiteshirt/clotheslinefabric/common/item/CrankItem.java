package com.jamieswhiteshirt.clotheslinefabric.common.item;

import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineBlocks;
import com.jamieswhiteshirt.clotheslinefabric.common.blockentity.ClotheslineAnchorBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class CrankItem extends Item {
    public CrankItem(Settings item$Settings_1) {
        super(item$Settings_1);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        if (ctx.getWorld().getBlockState(ctx.getPos()).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR) {
            ClotheslineAnchorBlockEntity blockEntity = ClotheslineAnchorBlock.getBlockEntity(ctx.getWorld(), ctx.getPos());
            if (blockEntity != null) {
                if (!blockEntity.getHasCrank()) {
                    blockEntity.setHasCrank(true);
                    ctx.getItemStack().subtractAmount(1);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.useOnBlock(ctx);
    }
}
