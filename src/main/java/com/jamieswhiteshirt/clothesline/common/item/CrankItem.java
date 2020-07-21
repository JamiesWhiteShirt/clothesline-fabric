package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class CrankItem extends Item {
    public CrankItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (state.getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR && !state.get(ClotheslineAnchorBlock.CRANK)) {
            ctx.getWorld().setBlockState(ctx.getBlockPos(), state.with(ClotheslineAnchorBlock.CRANK, true));
            ctx.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(ctx);
    }
}
