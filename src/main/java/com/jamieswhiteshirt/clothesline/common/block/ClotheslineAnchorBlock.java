package com.jamieswhiteshirt.clothesline.common.block;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.sound.ClotheslineSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ClotheslineAnchorBlock extends WallMountedBlock implements InventoryProvider, Waterloggable {
    private static final VoxelShape DOWN  = Block.createCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    private static final VoxelShape UP    = Block.createCuboidShape(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private static final VoxelShape NORTH = Block.createCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 16.0D);
    private static final VoxelShape SOUTH = Block.createCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 12.0D, 10.0D);
    private static final VoxelShape WEST  = Block.createCuboidShape(6.0D, 0.0D, 6.0D, 16.0D, 12.0D, 10.0D);
    private static final VoxelShape EAST  = Block.createCuboidShape(0.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final Property<Boolean> CRANK = BooleanProperty.of("crank");

    public ClotheslineAnchorBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(FACE, WallMountLocation.WALL).with(FACING, Direction.NORTH).with(WATERLOGGED, false).with(CRANK, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, WATERLOGGED, CRANK);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = getDirection(state);
        BlockPos neighborPos = pos.offset(direction.getOpposite());
        if (direction.getAxis() == Direction.Axis.Y) {
            return Block.sideCoversSmallSquare(world, neighborPos, direction);
        } else {
            return Block.isSideSolidFullSquare(world.getBlockState(neighborPos), world, neighborPos, direction);
        }
    }

    @Deprecated
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        switch (state.get(FACE)) {
            case FLOOR:
                return DOWN;
            case WALL:
                switch (state.get(FACING)) {
                    case NORTH:
                        return NORTH;
                    case SOUTH:
                        return SOUTH;
                    case WEST:
                        return WEST;
                    case EAST:
                    default:
                        return EAST;
                }
            case CEILING:
            default:
                return UP;
        }
    }

    @Deprecated
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() != state.getBlock()) {
            NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
            manager.createNode(pos);
        }
    }

    @Deprecated
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
            manager.breakNode(null, pos);
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        if (state != null) {
            FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
            return state.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        }
        return null;
    }

    @Deprecated
    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (state.get(CRANK)) {
            world.setBlockState(pos, state.with(CRANK, false));
            if (!world.isClient && !player.isCreative()) {
                dropStack(world, pos, new ItemStack(ClotheslineItems.CRANK));
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        manager.breakNode(player, pos);
        super.onBreak(world, pos, state, player);
    }

    @Deprecated
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (player.getStackInHand(hand).getItem() == ClotheslineItems.CLOTHESLINE) return ActionResult.PASS;

        if (state.get(CRANK)) {
            NetworkNode node = getNode(world, pos);
            if (node != null) {
                int momentumDelta = getCrankMultiplier(pos, hitResult.getPos().x, hitResult.getPos().z, player) * 5;
                NetworkState networkState = node.getNetwork().getState();
                networkState.setMomentum(networkState.getMomentum() + momentumDelta);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hitResult);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        NetworkNode node = getNode(world, pos);
        if (node != null) {
            int momentum = Math.abs(node.getNetwork().getState().getMomentum());
            float pitch = 0.2F + 0.6F * ((float)momentum / NetworkState.MAX_MOMENTUM) + random.nextFloat() * 0.1F;
            if (random.nextInt(12 * NetworkState.MAX_MOMENTUM) < momentum) {
                world.playSound(MinecraftClient.getInstance().player, pos, ClotheslineSoundEvents.BLOCK_CLOTHESLINE_ANCHOR_SQUEAK, SoundCategory.BLOCKS, 0.1F, pitch);
            }
        }
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return new ClotheslineAnchorInventory(((NetworkManagerProvider) world).getNetworkManager(), pos);
    }

    @Deprecated
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    private static NetworkNode getNode(World world, BlockPos pos) {
        return ((NetworkManagerProvider) world).getNetworkManager().getNetworks().getNodes().get(pos);
    }

    public static int getCrankMultiplier(BlockPos pos, double hitX, double hitZ, PlayerEntity player) {
        // Distance vector from the player to the center of the block
        double dxCenter = 0.5D + pos.getX() - player.getX();
        double dzCenter = 0.5D + pos.getZ() - player.getZ();
        // Distance vector from the player to the hit
        double dxHit = hitX - player.getX();
        double dzHit = hitZ - player.getZ();
        // Y component the cross product of the two vectors
        // The sign of the Y component indicates which "side" of the anchor is hit, which determines which way to crank
        double y = dzCenter * dxHit - dxCenter * dzHit;
        return (int) Math.signum(y);
    }

    private static class ClotheslineAnchorInventory implements SidedInventory {
        private final NetworkManager manager;
        private final BlockPos pos;

        private ClotheslineAnchorInventory(NetworkManager manager, BlockPos pos) {
            this.manager = manager;
            this.pos = pos;
        }

        @Nullable
        public NetworkNode getNetworkNode() {
            return manager.getNetworks().getNodes().get(pos);
        }

        @Override
        public int[] getAvailableSlots(Direction direction) {
            NetworkNode node = getNetworkNode();
            if (node != null && !node.getNetwork().getState().getPath().isEmpty()) {
                int attachmentKey = node.getNetwork().getState().offsetToAttachmentKey(node.getPathNode().getOffsetForDelta(direction.getVector()));
                List<MutableSortedIntMap.Entry<ItemStack>> entries = node.getNetwork().getState().getAttachmentsInRange(
                    attachmentKey - AttachmentUnit.UNITS_PER_BLOCK / 2,
                    attachmentKey + AttachmentUnit.UNITS_PER_BLOCK / 2
                );

                if (entries.isEmpty()) {
                    return new int[] { attachmentKey };
                } else {
                    return new int[] { entries.get(entries.size() / 2).getKey() };
                }
            }
            return new int[0];
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction direction) {
            NetworkNode node = getNetworkNode();
            if (node != null) {
                return node.getNetwork().insertItem(slot, stack, true).isEmpty();
            }
            return false;
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction direction) {
            return getNetworkNode() != null;
        }

        @Override
        public int size() {
            NetworkNode node = getNetworkNode();
            if (node != null) {
                return node.getNetwork().getState().getPathLength();
            }
            return 0;
        }

        @Override
        public boolean isEmpty() {
            NetworkNode node = getNetworkNode();
            if (node != null) {
                return node.getNetwork().getState().getAttachments().size() > 0;
            }
            return false;
        }

        @Override
        public ItemStack getStack(int slot) {
            NetworkNode node = getNetworkNode();
            if (node != null) {
                return node.getNetwork().getAttachment(slot);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return removeStack(slot);
        }

        @Override
        public ItemStack removeStack(int slot) {
            NetworkNode node = getNetworkNode();
            if (node != null) {
                return node.getNetwork().extractItem(slot, false);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            NetworkNode node = getNetworkNode();
            if (node != null) {
                node.getNetwork().insertItem(slot, stack, false);
            }
        }

        @Override
        public void markDirty() { }

        @Override
        public boolean canPlayerUse(PlayerEntity var1) {
            return false;
        }

        @Override
        public void clear() {
            manager.removeNode(pos);
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    }
}
