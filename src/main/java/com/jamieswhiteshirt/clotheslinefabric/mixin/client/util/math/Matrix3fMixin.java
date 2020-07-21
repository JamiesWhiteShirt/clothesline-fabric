package com.jamieswhiteshirt.clotheslinefabric.mixin.client.util.math;

import com.jamieswhiteshirt.clotheslinefabric.client.Matrix3fExtension;
import net.minecraft.util.math.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix3f.class)
public class Matrix3fMixin implements Matrix3fExtension {
    @Shadow protected float a00;
    @Shadow protected float a01;
    @Shadow protected float a02;
    @Shadow protected float a10;
    @Shadow protected float a11;
    @Shadow protected float a12;
    @Shadow protected float a20;
    @Shadow protected float a21;
    @Shadow protected float a22;

    @Override
    public void load(
        float a00, float a01, float a02,
        float a10, float a11, float a12,
        float a20, float a21, float a22
    ) {
        this.a00 = a00; this.a01 = a01; this.a02 = a02;
        this.a10 = a10; this.a11 = a11; this.a12 = a12;
        this.a20 = a20; this.a21 = a21; this.a22 = a22;
    }
}
