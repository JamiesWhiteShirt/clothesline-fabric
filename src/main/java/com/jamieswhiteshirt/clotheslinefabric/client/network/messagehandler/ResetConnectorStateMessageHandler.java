package com.jamieswhiteshirt.clotheslinefabric.client.network.messagehandler;

import com.jamieswhiteshirt.clotheslinefabric.common.network.message.ResetConnectorStateMessage;
import com.jamieswhiteshirt.clotheslinefabric.internal.ConnectorHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class ResetConnectorStateMessageHandler implements BiConsumer<PacketContext, ResetConnectorStateMessage> {
    @Override
    public void accept(PacketContext ctx, ResetConnectorStateMessage msg) {
        Entity entity = msg.entityId != -1 ? ctx.getPlayer().world.getEntityById(msg.entityId) : null;
        if (entity instanceof PlayerEntity) {
            ConnectorHolder connectorHolder = (ConnectorHolder) entity;
            connectorHolder.setFrom(null);
        }
    }
}
