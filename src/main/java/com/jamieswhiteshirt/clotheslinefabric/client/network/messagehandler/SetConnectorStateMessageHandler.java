package com.jamieswhiteshirt.clotheslinefabric.client.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.common.network.message.SetConnectorStateMessage;
import com.jamieswhiteshirt.clotheslinefabric.internal.ConnectorHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class SetConnectorStateMessageHandler implements BiConsumer<PacketContext, SetConnectorStateMessage> {
    @Override
    public void accept(PacketContext ctx, SetConnectorStateMessage msg) {
        Entity entity = msg.entityId != -1 ? ctx.getPlayer().world.getEntityById(msg.entityId) : null;
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            ConnectorHolder connectorHolder = (ConnectorHolder) entity;
            connectorHolder.setFrom(new ItemUsageContext(
                playerEntity,
                playerEntity.getStackInHand(msg.hand),
                msg.pos,
                msg.direction,
                msg.hitX,
                msg.hitY,
                msg.hitZ
            ));
        }
    }
}
