package com.jamieswhiteshirt.clotheslinefabric.mixin.client.network;

import com.jamieswhiteshirt.clotheslinefabric.common.item.ConnectorItem;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.StopUsingItemOnMessage;
import net.minecraft.class_3965;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.HitResult;
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
        method = "method_2897(Lnet/minecraft/entity/player/PlayerEntity;)V",
        cancellable = true
    )
    private void method_2897(PlayerEntity var1, CallbackInfo info) {
        info.cancel();
        HitResult hitResult = MinecraftClient.getInstance().hitResult;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (hitResult != null && world != null && hitResult.method_17783() == HitResult.Type.BLOCK) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.getActiveItem().getItem() instanceof ConnectorItem) {
                // This is a connector item, we must therefore tell the server which block position where the connection
                // will end.
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(
                    MessageChannels.STOP_USING_ITEM_ON.createServerboundPacket(new StopUsingItemOnMessage(
                        player.getActiveHand(),
                        (class_3965) hitResult
                    ))
                );

                ConnectorItem itemConnector = (ConnectorItem) player.getActiveItem().getItem();
                itemConnector.stopActiveHandWithTo(player, new ItemUsageContext(
                    player,
                    player.getActiveItem(),
                    (class_3965) hitResult
                ));

                // Cancel to avoid sending any more messages
                info.cancel();
            }
        }
    }
}
