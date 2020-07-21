package com.jamieswhiteshirt.clothesline.mixin.entity.player;

import com.jamieswhiteshirt.clothesline.internal.ConnectorHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ConnectorHolder {
    private ItemUsageContext connectFrom;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> var1, World var2) {
        super(var1, var2);
    }

    @Override
    public ItemUsageContext getFrom() {
        return connectFrom;
    }

    @Override
    public void setFrom(ItemUsageContext context) {
        this.connectFrom = context;
    }
}
