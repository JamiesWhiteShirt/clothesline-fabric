package com.jamieswhiteshirt.clothesline.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

/**
 * An interface for implementing rich block interactions.
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface RichBlockInteraction {
    /**
     * Returns the rich interaction type available in this context.
     * @param state the blockstate
     * @param world the world
     * @param pos the position of the block
     * @param player the interacting player
     * @param hitResult the interacting player's cursor position
     * @return the rich interaction available in this context
     */
    RichInteractionType getRichInteractionType(BlockState state, ClientWorld world, BlockPos pos, PlayerEntity player, BlockHitResult hitResult);
}
