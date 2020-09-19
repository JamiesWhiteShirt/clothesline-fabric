package com.jamieswhiteshirt.clothesline.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;

import org.jetbrains.annotations.Nullable;

/**
 * A registry for rich interactions. When the player cursor targets something with a rich interaction, the client may
 * display something to indicate that the targeted object has a rich interaction.
 *
 * By default, a {@link RichInteractionRegistry} may be retrieved by implementing a {@link Consumer} with the
 * "clothesline:rich_interaction" entrypoint. The entrypoint will be called only on the client at some point during
 * initialization.
 */
@Environment(EnvType.CLIENT)
public interface RichInteractionRegistry {
    /**
     * Register a rich block interaction. If a rich block interaction is already registered for the block, it will be
     * overwritten.
     * @param block the block
     * @param richBlockInteraction the rich interaction
     */
    void addBlock(Block block, RichBlockInteraction richBlockInteraction);

    /**
     * Get the rich block interaction for the block, or null if the block has none.
     * @param block the block
     * @return the rich block interaction for the block, or null if the block has none
     */
    @Nullable
    RichBlockInteraction getBlock(Block block);

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    interface Consumer {
        void accept(RichInteractionRegistry registry);
    }
}
