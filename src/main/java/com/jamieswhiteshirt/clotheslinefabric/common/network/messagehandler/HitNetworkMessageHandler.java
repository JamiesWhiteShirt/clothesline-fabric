package com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.api.*;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.HitNetworkMessage;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class HitNetworkMessageHandler implements BiConsumer<PacketContext, HitNetworkMessage> {
    @Override
    public void accept(PacketContext ctx, HitNetworkMessage message) {
        PlayerEntity player = ctx.getPlayer();
        World world = player.world;
        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        Network network = manager.getNetworks().getById(message.networkId);
        if (network != null) {
            Path.Edge edge = network.getState().getPath().getEdgeForPosition(message.offset);
            Vec3d pos = edge.getPositionForOffset(message.offset);
            if (Validation.canReachPos(player, pos)) {
                Line line = edge.getLine();
                manager.breakConnection(player, line.getFromPos(), line.getToPos());
                world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.BLOCK, 1.0F, 1.0F);
            }
        }
    }
}
