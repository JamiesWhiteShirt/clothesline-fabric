package com.jamieswhiteshirt.clotheslinefabric.common.block;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkNode;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkState;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.common.blockentity.ClotheslineAnchorBlockEntity;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clotheslinefabric.common.sound.ClotheslineSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class ClotheslineAnchorBlock extends WallMountedBlock implements BlockEntityProvider {
    private static final VoxelShape DOWN  = Block.createCubeShape(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    private static final VoxelShape UP    = Block.createCubeShape(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private static final VoxelShape NORTH = Block.createCubeShape(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 16.0D);
    private static final VoxelShape SOUTH = Block.createCubeShape(6.0D, 0.0D, 0.0D, 10.0D, 12.0D, 10.0D);
    private static final VoxelShape WEST  = Block.createCubeShape(6.0D, 0.0D, 6.0D, 16.0D, 12.0D, 10.0D);
    private static final VoxelShape EAST  = Block.createCubeShape(0.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);

    public ClotheslineAnchorBlock(Settings settings) {
        super(settings);
        setDefaultState(stateFactory.getDefaultState().with(field_11007, WallMountLocation.WALL).with(field_11177, Direction.NORTH).with(Properties.WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.with(field_11007, field_11177, Properties.WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getBoundingShape(BlockState state, BlockView view, BlockPos pos) {
        switch (state.get(field_11007)) {
            case FLOOR:
                return DOWN;
            case WALL:
                switch (state.get(field_11177)) {
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

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new ClotheslineAnchorBlockEntity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState state2) {
        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        manager.createNode(pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState state2, boolean var5) {
        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        manager.breakNode(null, pos);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        manager.breakNode(player, pos);
        super.onBreak(world, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float x, float y, float z) {
        if (player.getStackInHand(hand).getItem() == ClotheslineItems.CLOTHESLINE) return false;

        ClotheslineAnchorBlockEntity blockEntity = getBlockEntity(world, pos);
        if (blockEntity != null) {
            if (blockEntity.getHasCrank()) {
                blockEntity.crank(getCrankMultiplier(pos, pos.getX() + x, pos.getZ() + z, player) * 5);
                return true;
            }
        }
        return super.activate(state, world, pos, player, hand, direction, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        ClotheslineAnchorBlockEntity blockEntity = getBlockEntity(world, pos);
        if (blockEntity != null) {
            NetworkNode node = blockEntity.getNetworkNode();
            if (node != null) {
                int momentum = Math.abs(node.getNetwork().getState().getMomentum());
                float pitch = 0.2F + 0.6F * ((float)momentum / NetworkState.MAX_MOMENTUM) + random.nextFloat() * 0.1F;
                if (random.nextInt(12 * NetworkState.MAX_MOMENTUM) < momentum) {
                    world.playSound(MinecraftClient.getInstance().player, pos, ClotheslineSoundEvents.BLOCK_CLOTHESLINE_ANCHOR_SQUEAK, SoundCategory.BLOCK, 0.1F, pitch);
                }
            }
        }
    }

    @Nullable
    public static ClotheslineAnchorBlockEntity getBlockEntity(BlockView world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ClotheslineAnchorBlockEntity) {
            return (ClotheslineAnchorBlockEntity) tileEntity;
        }
        return null;
    }

    public static int getCrankMultiplier(BlockPos pos, double hitX, double hitZ, PlayerEntity player) {
        // Distance vector from the player to the center of the block
        double dxCenter = 0.5D + pos.getX() - player.x;
        double dzCenter = 0.5D + pos.getZ() - player.z;
        // Distance vector from the player to the hit
        double dxHit = hitX - player.x;
        double dzHit = hitZ - player.z;
        // Y component the cross product of the two vectors
        // The sign of the Y component indicates which "side" of the anchor is hit, which determines which way to crank
        double y = dzCenter * dxHit - dxCenter * dzHit;
        return (int) Math.signum(y);
    }
}
