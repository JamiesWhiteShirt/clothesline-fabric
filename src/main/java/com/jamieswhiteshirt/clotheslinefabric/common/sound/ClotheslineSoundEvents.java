package com.jamieswhiteshirt.clotheslinefabric.common.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ClotheslineSoundEvents {
    public static final SoundEvent BLOCK_CLOTHESLINE_ANCHOR_SQUEAK = register("block.clothesline_anchor.squeak");
    public static final SoundEvent BLOCK_CLOTHESLINE_ANCHOR_ROPE = register("block.clothesline_anchor.rope");

    private static SoundEvent register(String id) {
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(new Identifier("clothesline-fabric", id)));
    }

    public static void init() { }
}
