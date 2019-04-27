package com.jamieswhiteshirt.clotheslinefabric.common.block.entity;

import com.jamieswhiteshirt.clotheslinefabric.api.AttachmentUnit;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManager;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkNode;
import com.jamieswhiteshirt.clotheslinefabric.common.container.SpinnerContainer;
import com.jamieswhiteshirt.clotheslinefabric.common.recipe.ClotheslineRecipeTypes;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;

public class SpinnerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable {
    private static final int[] INPUT_SLOTS = new int[]{ 0 };
    private static final int[] OUTPUT_SLOTS = new int[]{ 1 };
    private static final int ATTACHMENT_UNITS_PER_CRAFT = AttachmentUnit.UNITS_PER_BLOCK * 20;

    private DefaultedList<ItemStack> inventory = DefaultedList.create(2, ItemStack.EMPTY);
    private int craftShift;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int id) {
            if (id == 0) return craftShift;
            return 0;
        }

        @Override
        public void set(int id, int value) {
            if (id == 0) craftShift = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public SpinnerBlockEntity() {
        super(ClotheslineBlockEntityTypes.SPINNER);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        inventory = DefaultedList.create(getInvSize(), ItemStack.EMPTY);
        Inventories.fromTag(tag, inventory);
        craftShift = tag.getShort("CraftShift");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, inventory);
        tag.putShort("CraftShift", (short) craftShift);
        return tag;
    }

    @Override
    public void tick() {
        if (!world.isClient) {
            Recipe<?> recipe = world.getRecipeManager().getFirstMatch(ClotheslineRecipeTypes.SPINNER, this, world).orElse(null);
            NetworkNode node = getNode();
            if (node != null && recipe != null) {
                int progress = node.getNetwork().getState().getShift() - craftShift;
                int steps = progress / ATTACHMENT_UNITS_PER_CRAFT;

                if (steps != 0) {
                    for (int i = 0; i < Math.abs(steps) && canAcceptRecipeOutput(recipe); i++) {
                        craftRecipe(recipe);
                    }

                    craftShift += steps * ATTACHMENT_UNITS_PER_CRAFT;

                    markDirty();
                }
            }
        }
    }

    private boolean canAcceptRecipeOutput(Recipe<?> recipe) {
        if (!inventory.get(0).isEmpty()) {
            ItemStack outputStack = recipe.getOutput();
            if (outputStack.isEmpty()) {
                return false;
            } else {
                ItemStack outputSlotStack = inventory.get(1);
                int resultingAmount = outputSlotStack.getAmount() + outputStack.getAmount();
                if (outputSlotStack.isEmpty()) {
                    return true;
                } else if (!outputSlotStack.isEqualIgnoreTags(outputStack)) {
                    return false;
                } else if (resultingAmount <= getInvMaxStackAmount() && resultingAmount <= outputSlotStack.getMaxAmount()) {
                    return true;
                } else {
                    return resultingAmount <= outputStack.getMaxAmount();
                }
            }
        } else {
            return false;
        }
    }

    private void craftRecipe(Recipe<?> recipe) {
        if (canAcceptRecipeOutput(recipe)) {
            ItemStack inputSlotStack = inventory.get(0);
            ItemStack outputStack = recipe.getOutput();
            ItemStack outputSlotStack = inventory.get(1);
            if (outputSlotStack.isEmpty()) {
                this.inventory.set(1, outputStack.copy());
            } else if (outputSlotStack.getItem() == outputStack.getItem()) {
                outputSlotStack.addAmount(outputStack.getAmount());
            }

            inputSlotStack.subtractAmount(1);
        }
    }

    @Nullable
    public NetworkNode getNode() {
        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        return manager.getNetworks().getNodes().get(pos.up());
    }

    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.clothesline-fabric.spinner");
    }

    @Override
    protected Container createContainer(int syncId, PlayerInventory playerInventory) {
        return new SpinnerContainer(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        return direction != Direction.DOWN ? INPUT_SLOTS : OUTPUT_SLOTS;
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction direction) {
        return isValidInvStack(slot, stack);
    }

    @Override
    public boolean canExtractInvStack(int var1, ItemStack var2, Direction var3) {
        return true;
    }

    @Override
    public int getInvSize() {
        return inventory.size();
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int count) {
        return Inventories.splitStack(inventory, slot, count);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setInvStack(int slotId, ItemStack stack) {
        ItemStack currentStack = inventory.get(slotId);
        boolean boolean_1 = !stack.isEmpty() && stack.isEqualIgnoreTags(currentStack) && ItemStack.areTagsEqual(stack, currentStack);
        inventory.set(slotId, stack);
        if (stack.getAmount() > getInvMaxStackAmount()) {
            stack.setAmount(getInvMaxStackAmount());
        }

        // TODO: Invalidate current progress
        /* if (slot == 0 && !boolean_1) {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
            this.markDirty();
        } */
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        if (world.getBlockEntity(pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 8.0D * 8.0D;
        }
    }

    @Override
    public boolean isValidInvStack(int slotId, ItemStack stack) {
        return slotId != 1;
    }

    @Override
    public void clear() {
        inventory.clear();
    }
}
