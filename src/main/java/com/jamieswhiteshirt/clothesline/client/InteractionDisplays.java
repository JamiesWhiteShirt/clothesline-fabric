package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.api.client.RichInteractionRegistry;
import com.jamieswhiteshirt.clothesline.api.client.RichInteractionType;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class InteractionDisplays implements RichInteractionRegistry.Consumer {
    @Override
    public void accept(RichInteractionRegistry registry) {
        registry.addBlock(ClotheslineBlocks.CLOTHESLINE_ANCHOR, (state, world, pos, player, hitResult) -> {
            if (state.get(ClotheslineAnchorBlock.CRANK)) {
                int crankMultiplier = ClotheslineAnchorBlock.getCrankMultiplier(pos, hitResult.getPos().x, hitResult.getPos().z, player);
                switch (crankMultiplier) {
                    case -1:
                        return RichInteractionType.ROTATE_CLOCKWISE;
                    case 1:
                        return RichInteractionType.ROTATE_COUNTER_CLOCKWISE;
                }
            }
            return RichInteractionType.NONE;
        });
    }
}
