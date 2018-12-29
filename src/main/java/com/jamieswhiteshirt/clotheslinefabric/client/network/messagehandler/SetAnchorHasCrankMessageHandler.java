package com.jamieswhiteshirt.clotheslinefabric.client.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.common.block.entity.ClotheslineAnchorBlockEntity;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.SetAnchorHasCrankMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class SetAnchorHasCrankMessageHandler implements BiConsumer<PacketContext, SetAnchorHasCrankMessage> {
    @Override
    public void accept(PacketContext ctx, SetAnchorHasCrankMessage msg) {
        World world = ctx.getPlayer().world;
        BlockEntity blockEntity = world.getBlockEntity(msg.pos);
        if (blockEntity instanceof ClotheslineAnchorBlockEntity) {
            ((ClotheslineAnchorBlockEntity) blockEntity).setHasCrank(msg.hasCrank);
        }
    }
}
