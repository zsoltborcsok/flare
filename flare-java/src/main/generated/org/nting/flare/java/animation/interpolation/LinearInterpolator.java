package org.nting.flare.java.animation.interpolation;

public class LinearInterpolator extends Interpolator {

    public static final LinearInterpolator INSTANCE = new LinearInterpolator();

    @Override
    public float getEasedMix(float mix) {
        return mix;
    }
}
