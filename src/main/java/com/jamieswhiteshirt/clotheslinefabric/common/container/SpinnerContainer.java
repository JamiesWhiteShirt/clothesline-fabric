package com.jamieswhiteshirt.clotheslinefabric.common.container;

import com.jamieswhiteshirt.clotheslinefabric.common.recipe.ClotheslineRecipeTypes;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SpinnerContainer extends CraftingContainer<Inventory> {
    public static final Identifier GUI_ID = new Identifier("clothesline-fabric", "spinner");

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final World world;

    public SpinnerContainer(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        // TODO: Should perhaps have a ContainerType?
        super(null, syncId);
        checkContainerSize(inventory, 2);
        checkContainerDataCount(propertyDelegate, 0);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.world;

        addSlot(new Slot(inventory, 0, 56, 35));
        addSlot(new SpinnerOutputSlot(inventory, 1, 116, 35));

        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for(int x = 0; x < 9; ++x) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }

        addProperties(propertyDelegate);
    }

    @Override
    public void populateRecipeFinder(RecipeFinder recipeFinder) {
        if (inventory instanceof RecipeInputProvider) {
            ((RecipeInputProvider)this.inventory).provideRecipeInputs(recipeFinder);
        }
    }

    @Override
    public void clearCraftingSlots() {
        inventory.clear();
    }

    @Override
    public void fillInputSlots(boolean boolean_1, Recipe<?> recipe_1, ServerPlayerEntity serverPlayerEntity_1) {
        // TODO
        // (new InputSlotFiller(this)).fillInputSlots(serverPlayerEntity_1, recipe_1, boolean_1);
    }

    @Override
    public boolean matches(Recipe<? super Inventory> recipe) {
        return recipe.matches(inventory, world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 1;
    }

    @Override
    public int getCraftingWidth() {
        return 1;
    }

    @Override
    public int getCraftingHeight() {
        return 1;
    }

    @Override
    public int getCraftingSlotCount() {
        return 2;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUseInv(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slotId) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slotList.get(slotId);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (slotId == 0) {
                if (!insertItem(slotStack, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotId == 1) {
                if (!insertItem(slotStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(slotStack, result);
            } else {
                if (isSpinnable(slotStack)) {
                    if (!insertItem(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotId >= 2 && slotId < 29) {
                    if (!insertItem(slotStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotId >= 29 && slotId < 38 && !this.insertItem(slotStack, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getAmount() == result.getAmount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return result;
    }

    private boolean isSpinnable(ItemStack stack) {
        return world.getRecipeManager().getFirstMatch(ClotheslineRecipeTypes.SPINNER, new BasicInventory(stack), world).isPresent();
    }
}
