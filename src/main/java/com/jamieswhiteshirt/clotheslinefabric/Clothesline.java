package com.jamieswhiteshirt.clotheslinefabric;

import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineBlocks;
import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkLoadEvent;
import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkWatchEvent;
import com.jamieswhiteshirt.clotheslinefabric.common.event.TrackEntityEvent;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.ServerMessageHandling;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.ResetConnectorStateMessage;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.SetConnectorStateMessage;
import com.jamieswhiteshirt.clotheslinefabric.common.sound.ClotheslineSoundEvents;
import com.jamieswhiteshirt.clotheslinefabric.internal.ConnectorHolder;
import com.jamieswhiteshirt.clotheslinefabric.internal.WorldExtension;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.events.TickEvent;
import net.minecraft.class_3965;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Clothesline implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("clothesline-fabric");

    @Override
    public void onInitialize() {
        ClotheslineBlocks.init();
        ClotheslineItems.init();
        ClotheslineSoundEvents.init();
        ServerMessageHandling.init();

        TickEvent.WORLD.register(world -> ((WorldExtension) world).clotheslineTick());
        ChunkWatchEvent.WATCH.register((world, pos, playerEntity) -> ((WorldExtension) world).onPlayerWatchChunk(pos, playerEntity));
        ChunkWatchEvent.UNWATCH.register((world, pos, playerEntity) -> ((WorldExtension) world).onPlayerUnWatchChunk(pos, playerEntity));
        ChunkLoadEvent.LOAD.register((world, pos) -> ((WorldExtension) world).onChunkLoaded(pos));
        ChunkLoadEvent.UNLOAD.register((world, pos) -> ((WorldExtension) world).onChunkUnloaded(pos));
        TrackEntityEvent.START.register((player, entity) -> {
            if (entity instanceof ConnectorHolder) {
                player.networkHandler.sendPacket(createConnectorStatePacket(((ConnectorHolder) entity).getFrom(), entity));
            }
        });
    }

    public static Hand getUsageHand(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if (player == null) return null;

        for (Hand hand : Hand.values()) {
            if (player.getStackInHand(hand) == ctx.getItemStack()) {
                return hand;
            }
        }
        return null;
    }

    public static CustomPayloadClientPacket createConnectorStatePacket(ItemUsageContext ctx, Entity entity) {
        if (ctx != null) {
            return MessageChannels.SET_CONNECTOR_STATE.createClientboundPacket(new SetConnectorStateMessage(
                entity.getEntityId(),
                getUsageHand(ctx),
                new class_3965(ctx.method_17698(), ctx.getFacing(), ctx.getPos(), ctx.method_17699())
            ));
        } else {
            return MessageChannels.RESET_CONNECTOR_STATE.createClientboundPacket(new ResetConnectorStateMessage(
                entity.getEntityId()
            ));
        }
    }
}
