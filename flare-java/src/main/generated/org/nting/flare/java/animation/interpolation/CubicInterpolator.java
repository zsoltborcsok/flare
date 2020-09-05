package org.nting.flare.java.animation.interpolation;

import org.nting.flare.java.StreamReader;

public class CubicInterpolator extends Interpolator {

    private CubicEase _cubic;

    @Override
    public float getEasedMix(float mix) {
        return _cubic.ease(mix);
    }

    public boolean read(StreamReader reader) {
        _cubic = CubicEase.make(reader.readFloat32("cubicX1"), reader.readFloat32("cubicY1"),
                reader.readFloat32("cubicX2"), reader.readFloat32("cubicY2"));
        return true;
    }
}
