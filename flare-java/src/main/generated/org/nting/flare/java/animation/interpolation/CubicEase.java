package org.nting.flare.java.animation.interpolation;

public abstract class CubicEase {

    public static CubicEase make(float x1, float y1, float x2, float y2) {
        if (x1 == y1 && x2 == y2) {
            return new LinearCubicEase();
        } else {
            return new Cubic(x1, y1, x2, y2);
        }
    }

    public abstract float ease(float t);
}
