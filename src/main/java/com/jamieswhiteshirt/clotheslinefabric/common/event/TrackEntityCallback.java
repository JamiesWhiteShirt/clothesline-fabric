package com.jamieswhiteshirt.clotheslinefabric.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface TrackEntityCallback {
    Event<TrackEntityCallback> START = EventFactory.createArrayBacked(TrackEntityCallback.class, (listeners) -> (player, entity) -> {
        for (TrackEntityCallback callback : listeners) {
            callback.accept(player, entity);
        }
    });

    void accept(ServerPlayerEntity player, Entity entity);
}
