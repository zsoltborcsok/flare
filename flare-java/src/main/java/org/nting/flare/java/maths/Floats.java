package org.nting.flare.java.maths;

import com.google.common.base.Preconditions;

public class Floats {

    /**
     * Returns this [num] clamped to be in the range [lowerLimit]-[upperLimit]. The arguments [lowerLimit] and
     * [upperLimit] must form a valid range where `lowerLimit < upperLimit`.
     */
    public static float clamp(float value, float lowerLimit, float upperLimit) {
        Preconditions.checkArgument(lowerLimit < upperLimit);

        if (lowerLimit <= value && value <= upperLimit) {
            return value;
        } else if (value < lowerLimit) {
            float range = upperLimit - lowerLimit;
            return value + (float) Math.ceil((lowerLimit - value) / range) * range;
        } else { // upperLimit < value
            float range = upperLimit - lowerLimit;
            return value - (float) Math.ceil((value - upperLimit) / range) * range;
        }
    }

}
