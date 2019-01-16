package com.jamieswhiteshirt.clotheslinefabric.client.raytrace;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkEdge;
import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public abstract class NetworkRaytraceHit {
    public final double distanceSq;
    public final NetworkEdge edge;

    public NetworkRaytraceHit(double distanceSq, NetworkEdge edge) {
        this.distanceSq = distanceSq;
        this.edge = edge;
    }

    public abstract boolean hitByEntity(PlayerEntity player);

    public abstract boolean useItem(PlayerEntity player, Hand hand);

    public abstract void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, float delta, double x, double y, double z, float r, float g, float b, float a);

    public abstract ItemStack getPickedResult();

    public abstract String getDebugString();
}
