package com.jamieswhiteshirt.clotheslinefabric.common.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkListener;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkState;
import com.jamieswhiteshirt.clotheslinefabric.internal.PersistentNetwork;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public final class NetworkImpl implements Network {
    private final int id;
    private final UUID uuid;
    private final NetworkState state;
    private final Map<Identifier, NetworkListener> eventListeners = new TreeMap<>();

    public NetworkImpl(int id, PersistentNetwork persistentNetwork) {
        this.id = id;
        this.uuid = persistentNetwork.getUuid();
        this.state = persistentNetwork.getState();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public NetworkState getState() {
        return state;
    }

    @Override
    public void update() {
        state.update();
    }

    @Override
    public boolean useItem(PlayerEntity player, Hand hand, int attachmentKey) {
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isEmpty()) {
            if (state.getAttachment(attachmentKey).isEmpty()) {
                player.setStackInHand(hand, insertItem(attachmentKey, stack, false));
                return true;
            }
        }
        return false;
    }

    @Override
    public void hitAttachment(PlayerEntity player, int attachmentKey) {
        ItemStack stack = state.getAttachment(attachmentKey);
        if (!stack.isEmpty()) {
            setAttachment(attachmentKey, ItemStack.EMPTY);
            World world = player.world;
            if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
                Vec3d pos = state.getPath().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey));
                ItemEntity itemEntity = new ItemEntity(world, pos.x, pos.y - 0.5D, pos.z, stack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }
    }

    @Override
    public ItemStack insertItem(int attachmentKey, ItemStack stack, boolean simulate) {
        if (!stack.isEmpty() && state.getAttachment(attachmentKey).isEmpty()) {
            if (!simulate) {
                ItemStack insertedItem = stack.copy();
                insertedItem.setAmount(1);
                setAttachment(attachmentKey, insertedItem);
            }

            ItemStack returnedStack = stack.copy();
            returnedStack.subtractAmount(1);
            return returnedStack;
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int attachmentKey, boolean simulate) {
        ItemStack result = state.getAttachment(attachmentKey);
        if (!result.isEmpty() && !simulate) {
            setAttachment(attachmentKey, ItemStack.EMPTY);
        }
        return result;
    }

    @Override
    public ItemStack getAttachment(int attachmentKey) {
        return state.getAttachment(attachmentKey);
    }

    @Override
    public void setAttachment(int attachmentKey, ItemStack stack) {
        ItemStack previousStack = state.getAttachment(attachmentKey);
        state.setAttachment(attachmentKey, stack);

        for (NetworkListener eventListener : eventListeners.values()) {
            eventListener.onAttachmentChanged(this, attachmentKey, previousStack, stack);
        }
    }

    @Override
    public void addEventListener(Identifier key, NetworkListener eventListener) {
        eventListeners.put(key, eventListener);
    }

    @Override
    public void removeEventListener(Identifier key) {
        eventListeners.remove(key);
    }
}
