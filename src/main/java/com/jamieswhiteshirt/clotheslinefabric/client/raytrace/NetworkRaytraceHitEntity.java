package com.jamieswhiteshirt.clotheslinefabric.client.raytrace;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.internal.PickStackEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public final class NetworkRaytraceHitEntity extends Entity implements PickStackEntity {
    private NetworkManager manager;
    private NetworkRaytraceHit hit;

    public NetworkRaytraceHitEntity(World world) {
        super(null, world);
    }

    @Override
    protected void initDataTracker() { }

    public NetworkRaytraceHitEntity(World world, NetworkManager manager, NetworkRaytraceHit hit) {
        this(world);
        this.manager = manager;
        this.hit = hit;
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag var1) { }

    @Override
    protected void writeCustomDataToTag(CompoundTag var1) { }

    @Override
    public boolean method_5698(Entity entity) {
        // Called when a player attacks the entity
        return hit.hitByEntity(manager, (PlayerEntity) entity);
    }

    @Override
    public boolean interact(PlayerEntity player, Hand hand) {
        return hit.useItem(manager, player, hand);
    }

    @Override
    public ItemStack getPickStack() {
        return hit.getPickedResult();
    }

    public NetworkRaytraceHit getHit() {
        return hit;
    }
}
