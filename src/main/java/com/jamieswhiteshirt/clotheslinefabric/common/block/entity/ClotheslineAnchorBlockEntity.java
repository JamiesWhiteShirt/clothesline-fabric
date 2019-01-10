package com.jamieswhiteshirt.clotheslinefabric.common.block.entity;

import com.jamieswhiteshirt.clotheslinefabric.api.*;
import com.jamieswhiteshirt.clotheslinefabric.api.util.MutableSortedIntMap;
import net.fabricmc.fabric.block.entity.ClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ClotheslineAnchorBlockEntity extends BlockEntity implements SidedInventory, Tickable, ClientSerializable {
    private NetworkManager manager;
    private boolean hasCrank;

    public ClotheslineAnchorBlockEntity() {
        super(ClotheslineBlockEntityTypes.CLOTHESLINE_ANCHOR);
    }

    public boolean getHasCrank() {
        return hasCrank;
    }

    public void setHasCrank(boolean hasCrank) {
        this.hasCrank = hasCrank;
        if (!world.isClient) {
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            // TODO: Use the custom message instead
            /* Clothesline.instance.networkChannel.sendToAllTracking(
                new SetAnchorHasCrankMessage(pos, hasCrank),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0)
            ); */
        }
    }


    @Nullable
    public NetworkNode getNetworkNode() {
        if (manager != null) {
            return manager.getNetworks().getNodes().get(pos);
        } else {
            return null;
        }
    }

    public void crank(int amount) {
        NetworkNode node = getNetworkNode();
        if (node != null) {
            NetworkState networkState = node.getNetwork().getState();
            networkState.setMomentum(networkState.getMomentum() + amount);
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        manager = ((NetworkManagerProvider) world).getNetworkManager();
    }

    @Override
    public void tick() {
        // crank(1);
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        super.toTag(compound);
        compound.putBoolean("HasCrank", hasCrank);
        return compound;
    }

    @Override
    public void fromTag(CompoundTag compound) {
        super.fromTag(compound);
        hasCrank = compound.getBoolean("HasCrank");
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        hasCrank = compoundTag.getBoolean("HasCrank");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        compoundTag.putBoolean("HasCrank", hasCrank);
        return compoundTag;
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
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
    public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction direction) {
        NetworkNode node = getNetworkNode();
        if (node != null) {
            return node.getNetwork().insertItem(slot, stack, true).isEmpty();
        }
        return false;
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction direction) {
        return getNetworkNode() != null;
    }

    @Override
    public int getInvSize() {
        NetworkNode node = getNetworkNode();
        if (node != null) {
            return node.getNetwork().getState().getPathLength();
        }
        return 0;
    }

    @Override
    public boolean isInvEmpty() {
        NetworkNode node = getNetworkNode();
        if (node != null) {
            return node.getNetwork().getState().getAttachments().size() > 0;
        }
        return false;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        NetworkNode node = getNetworkNode();
        if (node != null) {
            return node.getNetwork().getAttachment(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        return removeInvStack(slot);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        NetworkNode node = getNetworkNode();
        if (node != null) {
            return node.getNetwork().extractItem(slot, false);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        NetworkNode node = getNetworkNode();
        if (node != null) {
            node.getNetwork().insertItem(slot, stack, false);
        }
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity var1) {
        return false;
    }

    @Override
    public void clearInv() {
        manager.removeNode(pos);
    }

    @Override
    public int getInvMaxStackAmount() {
        return 1;
    }
}
