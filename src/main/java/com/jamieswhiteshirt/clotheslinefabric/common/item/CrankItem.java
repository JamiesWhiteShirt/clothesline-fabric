package com.jamieswhiteshirt.clotheslinefabric.common.item;

import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class CrankItem extends Item {
    public CrankItem(Settings item$Settings_1) {
        super(item$Settings_1);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        BlockState state = ctx.getWorld().getBlockState(ctx.getPos());
        if (state.getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR && !state.get(ClotheslineAnchorBlock.CRANK)) {
            ctx.getWorld().setBlockState(ctx.getPos(), state.with(ClotheslineAnchorBlock.CRANK, true));
            ctx.getItemStack().subtractAmount(1);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(ctx);
    }
}
