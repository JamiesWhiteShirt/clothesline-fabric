package com.jamieswhiteshirt.clotheslinefabric.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class Vec4f {
    private float v0;
    private float v1;
    private float v2;
    private float v3;

    public Vec4f() {
        this(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public Vec4f(float v0, float v1, float v2, float v3) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public void set(float v0, float v1, float v2, float v3) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public float getV0() {
        return v0;
    }

    public void setV0(float v0) {
        this.v0 = v0;
    }

    public float getV1() {
        return v1;
    }

    public void setV1(float v1) {
        this.v1 = v1;
    }

    public float getV2() {
        return v2;
    }

    public void setV2(float v2) {
        this.v2 = v2;
    }

    public float getV3() {
        return v3;
    }

    public void setV3(float v3) {
        this.v3 = v3;
    }

    public void multiply(Mat4f mat) {
        set(
            mat.getV00() * this.v0 + mat.getV10() * this.v1 + mat.getV20() * this.v2 + mat.getV30() * this.v3,
            mat.getV01() * this.v0 + mat.getV11() * this.v1 + mat.getV21() * this.v2 + mat.getV31() * this.v3,
            mat.getV02() * this.v0 + mat.getV12() * this.v1 + mat.getV22() * this.v2 + mat.getV32() * this.v3,
            mat.getV03() * this.v0 + mat.getV13() * this.v1 + mat.getV23() * this.v2 + mat.getV33() * this.v3
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec4f vec4f = (Vec4f) o;
        return Float.compare(vec4f.v0, v0) == 0 &&
            Float.compare(vec4f.v1, v1) == 0 &&
            Float.compare(vec4f.v2, v2) == 0 &&
            Float.compare(vec4f.v3, v3) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(v0, v1, v2, v3);
    }
}
