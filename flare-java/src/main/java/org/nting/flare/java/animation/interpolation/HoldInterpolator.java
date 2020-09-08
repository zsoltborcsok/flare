package org.nting.flare.java.animation.interpolation;

public class HoldInterpolator extends Interpolator {

    public static final HoldInterpolator INSTANCE = new HoldInterpolator();

    @Override
    public float getEasedMix(float mix) {
        return 0.0f;
    }
}
