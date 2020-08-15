package com.jamieswhiteshirt.clothesline.client.raytrace;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.EntityPickInteractionAware;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public final class NetworkRaytraceHitEntity extends Entity implements EntityPickInteractionAware {
    private static final EntityType<NetworkRaytraceHitEntity> ENTITY_TYPE = FabricEntityTypeBuilder.<NetworkRaytraceHitEntity>create(SpawnGroup.MISC, NetworkRaytraceHitEntity::new).dimensions(EntityDimensions.changing(0.25F, 0.25F)).build();

    private NetworkRaytraceHit hit;

    public NetworkRaytraceHitEntity(EntityType<NetworkRaytraceHitEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() { }

    public NetworkRaytraceHitEntity(World world, NetworkRaytraceHit hit) {
        this(ENTITY_TYPE, world);
        this.hit = hit;
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag var1) { }

    @Override
    protected void writeCustomDataToTag(CompoundTag var1) { }

    @Override
    public boolean handleAttack(Entity entity) {
        return hit.hitByEntity((PlayerEntity) entity);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return hit.useItem(player, hand);
    }

    public NetworkRaytraceHit getHit() {
        return hit;
    }

    @Override
    public ItemStack getPickedStack(PlayerEntity player, HitResult result) {
        return hit.getPickedResult();
    }
}
