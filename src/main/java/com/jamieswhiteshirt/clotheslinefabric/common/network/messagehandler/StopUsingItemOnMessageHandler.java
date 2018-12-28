package com.jamieswhiteshirt.clotheslinefabric.common.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.api.Utility;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ConnectorItem;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.StopUsingItemOnMessage;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;

import java.util.function.BiConsumer;

public class StopUsingItemOnMessageHandler implements BiConsumer<PacketContext, StopUsingItemOnMessage> {
    @Override
    public void accept(PacketContext ctx, StopUsingItemOnMessage msg) {
        PlayerEntity player = ctx.getPlayer();
        if (player.squaredDistanceTo(msg.pos.getX() + 0.5D, msg.pos.getY() + 0.5D, msg.pos.getZ() + 0.5D) >= 64.0D) {
            return;
        }

        if (player.getActiveItem().getItem() instanceof ConnectorItem) {
            ConnectorItem connectorItem = (ConnectorItem) player.getActiveItem().getItem();
            if (Validation.canReachPos(player, Utility.midVec(msg.pos))) {
                connectorItem.stopActiveHandWithTo(player, new ItemUsageContext(
                    player,
                    player.getActiveItem(),
                    msg.pos,
                    msg.direction,
                    msg.hitX,
                    msg.hitY,
                    msg.hitZ
                ));
            } else {
                player.method_6075();
            }
        } else {
            player.method_6075();
        }
    }
}
