package com.jamieswhiteshirt.clotheslinefabric.common.block;

import com.jamieswhiteshirt.clotheslinefabric.common.block.entity.SpinnerBlockEntity;
import com.jamieswhiteshirt.clotheslinefabric.common.container.SpinnerContainer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SpinnerBlock extends HorizontalFacingBlock implements BlockEntityProvider {

    public SpinnerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(stateFactory.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    @Deprecated
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (!world.isClient) {
            BlockEntity blockEntity_1 = world.getBlockEntity(pos);
            if (blockEntity_1 instanceof SpinnerBlockEntity) {
                ContainerProviderRegistry.INSTANCE.openContainer(SpinnerContainer.GUI_ID, player, buf -> buf.writeBlockPos(pos));
            }
        }
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerHorizontalFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.with(FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new SpinnerBlockEntity();
    }
}
