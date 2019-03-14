package com.jamieswhiteshirt.clotheslinefabric.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Mat4fTest {
    @Test
    void translateTranslatesVector() {
        Mat4f mat = Mat4f.translate(1.0F, 2.0F, 3.0F);

        Vec4f vec = new Vec4f(5.0F, 7.0F, 9.0F, 1.0F);
        vec.multiply(mat);
        Assertions.assertEquals(6.0F, vec.getV0());
        Assertions.assertEquals(9.0F, vec.getV1());
        Assertions.assertEquals(12.0F, vec.getV2());
        Assertions.assertEquals(1.0F, vec.getV3());
    }

    @Test
    void rotateXRotatesVector() {
        Mat4f mat = Mat4f.rotateX((float)Math.PI / 2.0F);

        Vec4f vec = new Vec4f(2.0F, 3.0F, 4.0F, 1.0F);
        vec.multiply(mat);
        Assertions.assertEquals(2.0F, vec.getV0());
        Assertions.assertEquals(-4.0F, vec.getV1(), 0.00001F);
        Assertions.assertEquals(3.0F, vec.getV2(), 0.00001F);
        Assertions.assertEquals(1.0F, vec.getV3());
    }

    @Test
    void rotateYRotatesVector() {
        Mat4f mat = Mat4f.rotateY((float)Math.PI / 2.0F);

        Vec4f vec = new Vec4f(2.0F, 3.0F, 4.0F, 1.0F);
        vec.multiply(mat);
        Assertions.assertEquals(4.0F, vec.getV0(), 0.00001F);
        Assertions.assertEquals(3.0F, vec.getV1());
        Assertions.assertEquals(-2.0F, vec.getV2(), 0.00001F);
        Assertions.assertEquals(1.0F, vec.getV3());
    }

    @Test
    void translateMultipledByScaleScalesThenTranslatesVector() {
        Mat4f mat = Mat4f.translate(1.0F, 2.0F, 3.0F);
        mat.multiply(Mat4f.scale(0.5F, 0.5F, 0.5F));

        Vec4f vec = new Vec4f(8.0F, 10.0F, 12.0F, 1.0F);
        vec.multiply(mat);
        Assertions.assertEquals(5.0F, vec.getV0());
        Assertions.assertEquals(7.0F, vec.getV1());
        Assertions.assertEquals(9.0F, vec.getV2());
        Assertions.assertEquals(1.0F, vec.getV3());
    }
}
