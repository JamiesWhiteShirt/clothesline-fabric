package com.jamieswhiteshirt.clotheslinefabric.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

import java.nio.FloatBuffer;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class Mat4f {
    private float v00;
    private float v01;
    private float v02;
    private float v03;
    private float v10;
    private float v11;
    private float v12;
    private float v13;
    private float v20;
    private float v21;
    private float v22;
    private float v23;
    private float v30;
    private float v31;
    private float v32;
    private float v33;

    public Mat4f(float v00, float v01, float v02, float v03, float v10, float v11, float v12, float v13, float v20, float v21, float v22, float v23, float v30, float v31, float v32, float v33) {
        this.v00 = v00;
        this.v01 = v01;
        this.v02 = v02;
        this.v03 = v03;
        this.v10 = v10;
        this.v11 = v11;
        this.v12 = v12;
        this.v13 = v13;
        this.v20 = v20;
        this.v21 = v21;
        this.v22 = v22;
        this.v23 = v23;
        this.v30 = v30;
        this.v31 = v31;
        this.v32 = v32;
        this.v33 = v33;
    }

    public void set(float v00, float v01, float v02, float v03, float v10, float v11, float v12, float v13, float v20, float v21, float v22, float v23, float v30, float v31, float v32, float v33) {
        this.v00 = v00;
        this.v01 = v01;
        this.v02 = v02;
        this.v03 = v03;
        this.v10 = v10;
        this.v11 = v11;
        this.v12 = v12;
        this.v13 = v13;
        this.v20 = v20;
        this.v21 = v21;
        this.v22 = v22;
        this.v23 = v23;
        this.v30 = v30;
        this.v31 = v31;
        this.v32 = v32;
        this.v33 = v33;
    }

    public float getV00() {
        return v00;
    }

    public void setV00(float v00) {
        this.v00 = v00;
    }

    public float getV01() {
        return v01;
    }

    public void setV01(float v01) {
        this.v01 = v01;
    }

    public float getV02() {
        return v02;
    }

    public void setV02(float v02) {
        this.v02 = v02;
    }

    public float getV03() {
        return v03;
    }

    public void setV03(float v03) {
        this.v03 = v03;
    }

    public float getV10() {
        return v10;
    }

    public void setV10(float v10) {
        this.v10 = v10;
    }

    public float getV11() {
        return v11;
    }

    public void setV11(float v11) {
        this.v11 = v11;
    }

    public float getV12() {
        return v12;
    }

    public void setV12(float v12) {
        this.v12 = v12;
    }

    public float getV13() {
        return v13;
    }

    public void setV13(float v13) {
        this.v13 = v13;
    }

    public float getV20() {
        return v20;
    }

    public void setV20(float v20) {
        this.v20 = v20;
    }

    public float getV21() {
        return v21;
    }

    public void setV21(float v21) {
        this.v21 = v21;
    }

    public float getV22() {
        return v22;
    }

    public void setV22(float v22) {
        this.v22 = v22;
    }

    public float getV23() {
        return v23;
    }

    public void setV23(float v23) {
        this.v23 = v23;
    }

    public float getV30() {
        return v30;
    }

    public void setV30(float v30) {
        this.v30 = v30;
    }

    public float getV31() {
        return v31;
    }

    public void setV31(float v31) {
        this.v31 = v31;
    }

    public float getV32() {
        return v32;
    }

    public void setV32(float v32) {
        this.v32 = v32;
    }

    public float getV33() {
        return v33;
    }

    public void setV33(float v33) {
        this.v33 = v33;
    }

    public void multiply(Mat4f that) {
        set(
            that.v00 * this.v00 + that.v01 * this.v10 + that.v02 * this.v20 + that.v03 * this.v30,
            that.v00 * this.v01 + that.v01 * this.v11 + that.v02 * this.v21 + that.v03 * this.v31,
            that.v00 * this.v02 + that.v01 * this.v12 + that.v02 * this.v22 + that.v03 * this.v32,
            that.v00 * this.v03 + that.v01 * this.v13 + that.v02 * this.v23 + that.v03 * this.v33,
            that.v10 * this.v00 + that.v11 * this.v10 + that.v12 * this.v20 + that.v13 * this.v30,
            that.v10 * this.v01 + that.v11 * this.v11 + that.v12 * this.v21 + that.v13 * this.v31,
            that.v10 * this.v02 + that.v11 * this.v12 + that.v12 * this.v22 + that.v13 * this.v32,
            that.v10 * this.v03 + that.v11 * this.v13 + that.v12 * this.v23 + that.v13 * this.v33,
            that.v20 * this.v00 + that.v21 * this.v10 + that.v22 * this.v20 + that.v23 * this.v30,
            that.v20 * this.v01 + that.v21 * this.v11 + that.v22 * this.v21 + that.v23 * this.v31,
            that.v20 * this.v02 + that.v21 * this.v12 + that.v22 * this.v22 + that.v23 * this.v32,
            that.v20 * this.v03 + that.v21 * this.v13 + that.v22 * this.v23 + that.v23 * this.v33,
            that.v30 * this.v00 + that.v31 * this.v10 + that.v32 * this.v20 + that.v33 * this.v30,
            that.v30 * this.v01 + that.v31 * this.v11 + that.v32 * this.v21 + that.v33 * this.v31,
            that.v30 * this.v02 + that.v31 * this.v12 + that.v32 * this.v22 + that.v33 * this.v32,
            that.v30 * this.v03 + that.v31 * this.v13 + that.v32 * this.v23 + that.v33 * this.v33
        );
    }

    public void putIntoBuffer(FloatBuffer buffer) {
        buffer.put(v00);
        buffer.put(v01);
        buffer.put(v02);
        buffer.put(v03);
        buffer.put(v10);
        buffer.put(v11);
        buffer.put(v12);
        buffer.put(v13);
        buffer.put(v20);
        buffer.put(v21);
        buffer.put(v22);
        buffer.put(v23);
        buffer.put(v30);
        buffer.put(v31);
        buffer.put(v32);
        buffer.put(v33);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mat4f mat4f = (Mat4f) o;
        return Float.compare(mat4f.v00, v00) == 0 &&
            Float.compare(mat4f.v01, v01) == 0 &&
            Float.compare(mat4f.v02, v02) == 0 &&
            Float.compare(mat4f.v03, v03) == 0 &&
            Float.compare(mat4f.v10, v10) == 0 &&
            Float.compare(mat4f.v11, v11) == 0 &&
            Float.compare(mat4f.v12, v12) == 0 &&
            Float.compare(mat4f.v13, v13) == 0 &&
            Float.compare(mat4f.v20, v20) == 0 &&
            Float.compare(mat4f.v21, v21) == 0 &&
            Float.compare(mat4f.v22, v22) == 0 &&
            Float.compare(mat4f.v23, v23) == 0 &&
            Float.compare(mat4f.v30, v30) == 0 &&
            Float.compare(mat4f.v31, v31) == 0 &&
            Float.compare(mat4f.v32, v32) == 0 &&
            Float.compare(mat4f.v33, v33) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(v00, v01, v02, v03, v10, v11, v12, v13, v20, v21, v22, v23, v30, v31, v32, v33);
    }

    public static Mat4f translate(float x, float y, float z) {
        return new Mat4f(
            1.0F, 0.0F, 0.0F, 0.0F,
            0.0F, 1.0F, 0.0F, 0.0F,
            0.0F, 0.0F, 1.0F, 0.0F,
            x, y, z, 1.0F
        );
    }

    public static Mat4f scale(float x, float y, float z) {
        return new Mat4f(
            x, 0.0F, 0.0F, 0.0F,
            0.0F,    y, 0.0F, 0.0F,
            0.0F, 0.0F,    z, 0.0F,
            0.0F, 0.0F, 0.0F, 1.0F
        );
    }

    public static Mat4f rotateY(float angle) {
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);

        return new Mat4f(
            cos, 0.0F, -sin, 0.0F,
            0.0F, 1.0F, 0.0F, 0.0F,
            sin, 0.0F,  cos, 0.0F,
            0.0F, 0.0F, 0.0F, 1.0F
        );
    }

    public static Mat4f rotateX(float angle) {
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);

        return new Mat4f(
            1.0F, 0.0F, 0.0F, 0.0F,
            0.0F, cos, sin, 0.0F,
            0.0F, -sin, cos, 0.0F,
            0.0F, 0.0F, 0.0F, 1.0F
        );
    }
}
