package com.jamieswhiteshirt.clothesline.client;

public final class InvertibleTransformation {
    private final Transformation forward;
    private final Transformation inverse;

    public InvertibleTransformation(Transformation forward, Transformation inverse) {
        this.forward = forward;
        this.inverse = inverse;
    }

    public Transformation getForward() {
        return forward;
    }

    public Transformation getInverse() {
        return inverse;
    }
}
