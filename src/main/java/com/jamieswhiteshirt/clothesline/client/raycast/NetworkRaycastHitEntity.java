package com.jamieswhiteshirt.clothesline.client.raycast;

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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public final class NetworkRaycastHitEntity extends Entity implements EntityPickInteractionAware {
    private static final EntityType<NetworkRaycastHitEntity> ENTITY_TYPE = FabricEntityTypeBuilder.<NetworkRaycastHitEntity>create(SpawnGroup.MISC, NetworkRaycastHitEntity::new).dimensions(EntityDimensions.changing(0.25F, 0.25F)).build();

    private NetworkRaycastHit hit;

    public NetworkRaycastHitEntity(EntityType<NetworkRaycastHitEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() { }

    public NetworkRaycastHitEntity(World world, NetworkRaycastHit hit) {
        this(ENTITY_TYPE, world);
        this.hit = hit;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound var1) { }

    @Override
    protected void writeCustomDataToNbt(NbtCompound var1) { }

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

    public NetworkRaycastHit getHit() {
        return hit;
    }

    @Override
    public ItemStack getPickedStack(PlayerEntity player, HitResult result) {
        return hit.getPickedResult();
    }
}
