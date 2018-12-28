package com.jamieswhiteshirt.clotheslinefabric.impl;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkState;
import com.jamieswhiteshirt.clotheslinefabric.api.AttachmentUnit;
import com.jamieswhiteshirt.clotheslinefabric.api.Path;
import com.jamieswhiteshirt.clotheslinefabric.api.Tree;
import com.jamieswhiteshirt.clotheslinefabric.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clotheslinefabric.common.impl.NetworkStateImpl;
import com.jamieswhiteshirt.clotheslinefabric.common.util.ChunkSpan;
import com.jamieswhiteshirt.clotheslinefabric.common.util.PathBuilder;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class NetworkStateTest {
    @BeforeAll
    static void bootstrap() {
        Bootstrap.initialize();
    }

    NetworkState state;

    // @BeforeEach
    void resetState() {
        BlockPos from = new BlockPos(0, 0, 0);
        BlockPos to = new BlockPos(1, 0, 0);
        Tree tree = new Tree(
            from,
            Collections.singletonList(new Tree.Edge(
                to.subtract(from),
                AttachmentUnit.lengthBetween(from, to),
                0,
                Tree.empty(to, AttachmentUnit.UNITS_PER_BLOCK, 0))
            ),
            0, AttachmentUnit.UNITS_PER_BLOCK * 2, 0
        );
        Path path = PathBuilder.buildPath(tree);
        LongSet chunkSpan = ChunkSpan.ofPath(path);
        MutableSortedIntMap<ItemStack> attachments = MutableSortedIntMap.empty(AttachmentUnit.UNITS_PER_BLOCK * 2);
        state = new NetworkStateImpl(0, 0, 0, 0, tree, path, chunkSpan, attachments);
    }

    void assertItemStacksEqual(ItemStack expected, ItemStack actual) {
        Assertions.assertTrue(ItemStack.areEqual(expected, actual));
    }

    // @Test
    void unsetItemsAreEmpty() {
        assertItemStacksEqual(state.getAttachment(0), ItemStack.EMPTY);
    }
}
