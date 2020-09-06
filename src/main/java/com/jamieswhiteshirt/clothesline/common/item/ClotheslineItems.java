package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.api.Utility;
import com.jamieswhiteshirt.clothesline.common.Util;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class ClotheslineItems {
    public static final Item CLOTHESLINE_ANCHOR = registerBlock(ClotheslineBlocks.CLOTHESLINE_ANCHOR, ClotheslineItemGroups.ITEMS);
    public static final Item CLOTHESLINE = register("clothesline", new ConnectorItem(new Item.Settings().group(ClotheslineItemGroups.ITEMS), new ConnectorItem.ConnectorBehavior() {
        @Override
        public boolean canConnectFrom(ItemUsageContext from) {
            return from.getWorld().getBlockState(from.getBlockPos()).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR;
        }

        @Override
        public boolean connect(ItemUsageContext from, ItemUsageContext to) {
            World world = to.getWorld();
            NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
            if (world.getBlockState(to.getBlockPos()).getBlock() == ClotheslineBlocks.CLOTHESLINE_ANCHOR) {
                Vec3d fromVec = Utility.midVec(from.getBlockPos());
                Vec3d toVec = Utility.midVec(to.getBlockPos());
                BlockHitResult hitResult = world.raycast(new RaycastContext(fromVec, toVec, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, to.getPlayer()));
                if (hitResult.getType() == HitResult.Type.MISS) {
                    if (manager.connect(from.getBlockPos(), to.getBlockPos())) {
                        if (!Util.isCreativePlayer(to.getPlayer())) {
                            to.getStack().decrement(1);
                        }
                        world.playSound(to.getPlayer(), to.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return true;
                    }
                }
            }
            return false;
        }
    }));
    public static final Item CRANK = register("crank", new CrankItem(new Item.Settings().group(ClotheslineItemGroups.ITEMS)));
    public static final Item SPINNER = register("spinner", new SpinnerItem(new Item.Settings().group(ClotheslineItemGroups.ITEMS)));

    private static Item registerBlock(Block block) {
        return register(new BlockItem(block, new Item.Settings()));
    }

    private static Item registerBlock(Block block, ItemGroup itemGroup) {
        return register(new BlockItem(block, (new Item.Settings()).group(itemGroup)));
    }

    private static Item register(BlockItem item) {
        return register(item.getBlock(), item);
    }

    private static Item register(Block block, Item item) {
        return register(Registry.BLOCK.getId(block), item);
    }

    private static Item register(String id, Item item) {
        return register(new Identifier("clothesline", id), item);
    }

    private static Item register(Identifier id, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return Registry.register(Registry.ITEM, id, item);
    }

    public static void init() { }
}
