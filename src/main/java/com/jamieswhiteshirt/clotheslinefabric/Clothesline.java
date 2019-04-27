package com.jamieswhiteshirt.clotheslinefabric;

import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineBlocks;
import com.jamieswhiteshirt.clotheslinefabric.common.block.entity.ClotheslineBlockEntityTypes;
import com.jamieswhiteshirt.clotheslinefabric.common.block.entity.SpinnerBlockEntity;
import com.jamieswhiteshirt.clotheslinefabric.common.container.SpinnerContainer;
import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkLoadCallback;
import com.jamieswhiteshirt.clotheslinefabric.common.event.ChunkWatchCallback;
import com.jamieswhiteshirt.clotheslinefabric.common.event.TrackEntityCallback;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.ServerMessageHandling;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.ResetConnectorStateMessage;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.SetConnectorStateMessage;
import com.jamieswhiteshirt.clotheslinefabric.common.recipe.ClotheslineRecipeSerializers;
import com.jamieswhiteshirt.clotheslinefabric.common.recipe.ClotheslineRecipeTypes;
import com.jamieswhiteshirt.clotheslinefabric.common.sound.ClotheslineSoundEvents;
import com.jamieswhiteshirt.clotheslinefabric.internal.ConnectorHolder;
import com.jamieswhiteshirt.clotheslinefabric.internal.WorldExtension;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.container.ContainerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Clothesline implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("clothesline-fabric");

    @Override
    public void onInitialize() {
        ClotheslineBlocks.init();
        ClotheslineBlockEntityTypes.init();
        ClotheslineItems.init();
        ClotheslineRecipeTypes.init();
        ClotheslineRecipeSerializers.init();
        ClotheslineSoundEvents.init();
        ServerMessageHandling.init();

        WorldTickCallback.EVENT.register(world -> ((WorldExtension) world).clotheslineTick());
        ChunkWatchCallback.WATCH.register((world, pos, playerEntity) -> ((WorldExtension) world).onPlayerWatchChunk(pos, playerEntity));
        ChunkWatchCallback.UNWATCH.register((world, pos, playerEntity) -> ((WorldExtension) world).onPlayerUnWatchChunk(pos, playerEntity));
        ChunkLoadCallback.LOAD.register((world, pos) -> ((WorldExtension) world).onChunkLoaded(pos));
        ChunkLoadCallback.UNLOAD.register((world, pos) -> ((WorldExtension) world).onChunkUnloaded(pos));
        TrackEntityCallback.START.register((player, entity) -> {
            if (entity instanceof ConnectorHolder) {
                player.networkHandler.sendPacket(createConnectorStatePacket(((ConnectorHolder) entity).getFrom(), entity));
            }
        });

        ContainerProviderRegistry.INSTANCE.registerFactory(SpinnerContainer.GUI_ID, (syncId, identifier, player, buf) -> {
            BlockPos pos = buf.readBlockPos();
            BlockEntity blockEntity = player.world.getBlockEntity(pos);
            if (blockEntity instanceof SpinnerBlockEntity) {
                return ((SpinnerBlockEntity) blockEntity).createMenu(syncId, player.inventory, player);
            }
            return null;
        });
    }

    public static CustomPayloadS2CPacket createConnectorStatePacket(ItemUsageContext ctx, Entity entity) {
        if (ctx != null) {
            return MessageChannels.SET_CONNECTOR_STATE.createClientboundPacket(new SetConnectorStateMessage(
                entity.getEntityId(),
                ctx.getHand(),
                new BlockHitResult(ctx.getPos(), ctx.getFacing(), ctx.getBlockPos(), ctx.method_17699())
            ));
        } else {
            return MessageChannels.RESET_CONNECTOR_STATE.createClientboundPacket(new ResetConnectorStateMessage(
                entity.getEntityId()
            ));
        }
    }
}
