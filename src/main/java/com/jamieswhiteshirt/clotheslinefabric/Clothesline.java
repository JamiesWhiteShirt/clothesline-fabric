package com.jamieswhiteshirt.clotheslinefabric;

import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineBlocks;
import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkWatchCallback;
import com.jamieswhiteshirt.clotheslinefabric.common.event.TrackEntityCallback;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.ServerMessageHandling;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.ResetConnectorStateMessage;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.SetConnectorStateMessage;
import com.jamieswhiteshirt.clotheslinefabric.common.sound.ClotheslineSoundEvents;
import com.jamieswhiteshirt.clotheslinefabric.internal.ConnectorHolder;
import com.jamieswhiteshirt.clotheslinefabric.internal.WorldExtension;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
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

        ServerTickEvents.END_WORLD_TICK.register(world -> ((WorldExtension) world).clotheslineTick());
        ClientTickEvents.END_WORLD_TICK.register(world -> ((WorldExtension) world).clotheslineTick());
        ChunkWatchCallback.WATCH.register((world, pos, playerEntity) -> ((WorldExtension) world).onPlayerWatchChunk(pos, playerEntity));
        ChunkWatchCallback.UNWATCH.register((world, pos, playerEntity) -> ((WorldExtension) world).onPlayerUnWatchChunk(pos, playerEntity));
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> ((WorldExtension) world).onChunkLoaded(chunk.getPos()));
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> ((WorldExtension) world).onChunkUnloaded(chunk.getPos()));
        TrackEntityCallback.START.register((player, entity) -> {
            if (entity instanceof ConnectorHolder) {
                player.networkHandler.sendPacket(createConnectorStatePacket(((ConnectorHolder) entity).getFrom(), entity));
            }
        });
    }

    public static CustomPayloadS2CPacket createConnectorStatePacket(ItemUsageContext ctx, Entity entity) {
        if (ctx != null) {
            return MessageChannels.SET_CONNECTOR_STATE.createClientboundPacket(new SetConnectorStateMessage(
                entity.getEntityId(),
                ctx.getHand(),
                new BlockHitResult(ctx.getHitPos(), ctx.getSide(), ctx.getBlockPos(), ctx.hitsInsideBlock())
            ));
        } else {
            return MessageChannels.RESET_CONNECTOR_STATE.createClientboundPacket(new ResetConnectorStateMessage(
                entity.getEntityId()
            ));
        }
    }
}
