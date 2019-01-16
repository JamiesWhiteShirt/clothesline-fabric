package com.jamieswhiteshirt.clotheslinefabric.mixin.client;

import com.jamieswhiteshirt.clotheslinefabric.internal.PickStackEntity;
import net.minecraft.class_3966;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/SpawnEggItem;method_8019(Lnet/minecraft/entity/EntityType;)Lnet/minecraft/item/SpawnEggItem;"
        ),
        method = "doItemPick()V"
    )
    private SpawnEggItem substitutePhantomEggToAvoidReturn(EntityType<?> entityType) {
        if (((class_3966) ((MinecraftClient) (Object) this).hitResult).method_17782() instanceof PickStackEntity) {
            return (SpawnEggItem) Items.PHANTOM_SPAWN_EGG;
        } else {
            return SpawnEggItem.method_8019(entityType);
        }
    }

    @Redirect(
        at = @At(
            value = "NEW",
            target = "net/minecraft/item/ItemStack"
        ),
        method = "doItemPick()V"
    )
    private ItemStack constructTheRightItemStack(ItemProvider provider) {
        Entity entity = ((class_3966) ((MinecraftClient) (Object) this).hitResult).method_17782();
        if (entity instanceof PickStackEntity) {
            return ((PickStackEntity) entity).getPickStack();
        }
        return new ItemStack(provider);
    }
}
