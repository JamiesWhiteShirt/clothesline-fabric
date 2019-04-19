package com.jamieswhiteshirt.clotheslinefabric.mixin.client.network;

import com.jamieswhiteshirt.clotheslinefabric.common.item.ConnectorItem;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.StopUsingItemOnMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"
        ),
        method = "stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V",
        cancellable = true
    )
    private void stopUsingItem(PlayerEntity var1, CallbackInfo info) {
        HitResult hitResult = MinecraftClient.getInstance().hitResult;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (hitResult != null && world != null && hitResult.getType() == HitResult.Type.BLOCK) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.getActiveItem().getItem() instanceof ConnectorItem) {
                // This is a connector item, we must therefore tell the server which block position where the connection
                // will end.
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(
                    MessageChannels.STOP_USING_ITEM_ON.createServerboundPacket(new StopUsingItemOnMessage(
                        player.getActiveHand(),
                        (BlockHitResult) hitResult
                    ))
                );

                ConnectorItem itemConnector = (ConnectorItem) player.getActiveItem().getItem();
                itemConnector.stopActiveHandWithTo(player, new ItemUsageContext(
                    player,
                    player.getActiveHand(),
                    (BlockHitResult) hitResult
                ));

                // Cancel to avoid sending any more messages
                info.cancel();
            }
        }
    }
}
