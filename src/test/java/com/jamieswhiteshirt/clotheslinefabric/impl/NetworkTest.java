package com.jamieswhiteshirt.clotheslinefabric.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkListener;
import com.jamieswhiteshirt.clotheslinefabric.internal.PersistentNetwork;
import com.jamieswhiteshirt.clotheslinefabric.common.impl.NetworkImpl;
import com.jamieswhiteshirt.clotheslinefabric.common.util.NetworkStateBuilder;
import net.minecraft.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.UUID;

class NetworkTest {
    // @BeforeAll
    static void bootstrap() {
        Bootstrap.initialize();
    }

    NetworkImpl network;
    Identifier eventListenerKey = new Identifier("test", "test");
    // @BeforeEach
    void resetNetwork() {
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(1, 0, 0);
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, posA);
        stateBuilder.addEdge(posA, posB);
        network = new NetworkImpl(0, new PersistentNetwork(new UUID(0, 0), stateBuilder.build()));
    }

    void assertItemStacksEqual(ItemStack expected, ItemStack actual) {
        Assertions.assertTrue(ItemStack.areEqual(expected, actual));
    }

    // @Test
    void maxStackSizeIsZero() {
        ItemStack stack = new ItemStack(Items.STICK, 3);
        assertItemStacksEqual(new ItemStack(Items.STICK, 2), network.insertItem(0, stack, false));
        assertItemStacksEqual(new ItemStack(Items.STICK, 1), network.getAttachment(0));
        assertItemStacksEqual(stack, network.insertItem(0, stack, false));
    }

    // @Test
    void simulationDoesNotChangeState() {
        ItemStack stack = new ItemStack(Items.STICK);
        assertItemStacksEqual(ItemStack.EMPTY, network.insertItem(0, stack, true));
        assertItemStacksEqual(ItemStack.EMPTY, network.getAttachment(0));

        network.insertItem(0, stack, false);

        assertItemStacksEqual(stack, network.extractItem(0, true));
        assertItemStacksEqual(stack, network.getAttachment(0));
    }

    // @Test
    void simulationDoesNotFireEvents() {
        network.insertItem(1, new ItemStack(Items.STICK), false);

        NetworkListener eventListener = Mockito.mock(NetworkListener.class);
        network.addEventListener(eventListenerKey, eventListener);

        network.insertItem(0, new ItemStack(Items.STICK), true);
        network.extractItem(1, true);

        Mockito.verifyZeroInteractions(eventListener);
    }

    ArgumentMatcher<ItemStack> itemStackEquals(ItemStack expected) {
        return actual -> ItemStack.areEqual(actual, expected);
    }

    // @Test
    void firesEventForAttachmentChange() {
        NetworkListener eventListener = Mockito.mock(NetworkListener.class);
        network.addEventListener(eventListenerKey, eventListener);

        ItemStack stack1 = new ItemStack(Items.APPLE);
        ItemStack stack2 = new ItemStack(Items.STICK);

        network.insertItem(0, stack1, false);
        network.insertItem(1, stack2, false);
        Mockito.verify(eventListener).onAttachmentChanged(
            ArgumentMatchers.eq(network),
            ArgumentMatchers.eq(0),
            ArgumentMatchers.argThat(itemStackEquals(ItemStack.EMPTY)),
            ArgumentMatchers.argThat(itemStackEquals(stack1))
        );
        Mockito.verify(eventListener).onAttachmentChanged(
            ArgumentMatchers.eq(network),
            ArgumentMatchers.eq(1),
            ArgumentMatchers.argThat(itemStackEquals(ItemStack.EMPTY)),
            ArgumentMatchers.argThat(itemStackEquals(stack2))
        );

        network.extractItem(0, false);
        Mockito.verify(eventListener).onAttachmentChanged(
            ArgumentMatchers.eq(network),
            ArgumentMatchers.eq(0),
            ArgumentMatchers.argThat(itemStackEquals(stack1)),
            ArgumentMatchers.argThat(itemStackEquals(ItemStack.EMPTY))
        );

        network.removeEventListener(eventListenerKey);

        network.extractItem(1, false);
        Mockito.verifyZeroInteractions(eventListener);
    }
}
