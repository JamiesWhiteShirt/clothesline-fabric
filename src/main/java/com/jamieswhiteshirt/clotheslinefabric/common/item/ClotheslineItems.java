package com.jamieswhiteshirt.clotheslinefabric.common.item;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.api.Utility;
import com.jamieswhiteshirt.clotheslinefabric.common.Util;
import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineBlocks;
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
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class ClotheslineItems {
    public static final Item CLOTHESLINE_ANCHOR = registerBlock(ClotheslineBlocks.CLOTHESLINE_ANCHOR, ClotheslineItemGroups.ITEMS);
    public static final Item CLOTHESLINE = register("clothesline", new ConnectorItem(new Item.Settings().itemGroup(ClotheslineItemGroups.ITEMS), new ConnectorItem.ConnectorBehavior() {
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
                BlockHitResult hitResult = world.rayTrace(new RayTraceContext(fromVec, toVec, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, to.getPlayer()));
                if (hitResult.getType() == HitResult.Type.MISS) {
                    if (manager.connect(from.getBlockPos(), to.getBlockPos())) {
                        if (!Util.isCreativePlayer(to.getPlayer())) {
                            to.getItemStack().subtractAmount(1);
                        }
                        world.playSound(to.getPlayer(), to.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return true;
                    }
                }
            }
            return false;
        }
    }));
    public static final Item CRANK = register("crank", new CrankItem(new Item.Settings().itemGroup(ClotheslineItemGroups.ITEMS)));
    public static final Item SPINNER = register("spinner", new SpinnerItem(new Item.Settings().itemGroup(ClotheslineItemGroups.ITEMS)));

    private static Item registerBlock(Block block) {
        return register(new BlockItem(block, new Item.Settings()));
    }

    private static Item registerBlock(Block block, ItemGroup itemGroup) {
        return register(new BlockItem(block, (new Item.Settings()).itemGroup(itemGroup)));
    }

    private static Item register(BlockItem item) {
        return register(item.getBlock(), item);
    }

    private static Item register(Block block, Item item) {
        return register(Registry.BLOCK.getId(block), item);
    }

    private static Item register(String id, Item item) {
        return register(new Identifier("clothesline-fabric", id), item);
    }

    private static Item register(Identifier id, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).registerBlockItemMap(Item.BLOCK_ITEM_MAP, item);
        }

        return Registry.register(Registry.ITEM, id, item);
    }

    public static void init() { }
}
