package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.client.RichBlockInteraction;
import com.jamieswhiteshirt.clothesline.api.client.RichInteractionRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RichInteractionRegistryImpl implements RichInteractionRegistry {
    private final Map<Block, RichBlockInteraction> blocks = new HashMap<>();

    @Override
    public void addBlock(Block block, RichBlockInteraction richBlockInteraction) {
        blocks.put(block, richBlockInteraction);
    }

    @Override
    public RichBlockInteraction getBlock(Block block) {
        return blocks.get(block);
    }
}
