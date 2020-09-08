package org.nting.flare.java.animation.interpolation;

import static java.lang.Math.abs;

public class Cubic extends CubicEase {

    private static final int newtonIterations = 4;
    private static final float newtonMinSlope = 0.001f;
    private static final float subdivisionPrecision = 0.0000001f;
    private static final int subdivisionMaxIterations = 10;

    private static final int splineTableSize = 11;
    private static final float sampleStepSize = 1.0f / (splineTableSize - 1.0f);

    private final float[] _values;
    private final float x1, y1, x2, y2;

    public Cubic(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        // Precompute values table
        _values = new float[splineTableSize];
        for (int i = 0; i < splineTableSize; ++i) {
            _values[i] = calcBezier(i * sampleStepSize, x1, x2);
        }
    }

    @Override
    public float ease(float mix) {
        return calcBezier(getT(mix), y1, y2);
    }

    private float getT(float x) {
        float intervalStart = 0.0f;
        int currentSample = 1;
        int lastSample = splineTableSize - 1;

        for (; currentSample != lastSample && _values[currentSample] <= x; ++currentSample) {
            intervalStart += sampleStepSize;
        }
        --currentSample;

        // Interpolate to provide an initial guess for t
        float dist = (x - _values[currentSample]) / (_values[currentSample + 1] - _values[currentSample]);
        float guessForT = intervalStart + dist * sampleStepSize;

        float initialSlope = getSlope(guessForT, x1, x2);
        if (initialSlope >= newtonMinSlope) {
            for (int i = 0; i < newtonIterations; ++i) {
                float currentSlope = getSlope(guessForT, x1, x2);
                if (currentSlope == 0.0f) {
                    return guessForT;
                }
                float currentX = calcBezier(guessForT, x1, x2) - x;
                guessForT -= currentX / currentSlope;
            }
            return guessForT;
        } else if (initialSlope == 0.0f) {
            return guessForT;
        } else {
            float aB = intervalStart + sampleStepSize;
            float currentX, currentT;
            int i = 0;
            do {
                currentT = intervalStart + (aB - intervalStart) / 2.0f;
                currentX = calcBezier(currentT, x1, x2) - x;
                if (currentX > 0.0f) {
                    aB = currentT;
                } else {
                    intervalStart = currentT;
                }
            } while (abs(currentX) > subdivisionPrecision && ++i < subdivisionMaxIterations);
            return currentT;
        }
    }

    // Returns x(t) given t, x1, and x2, or y(t) given t, y1, and y2.
    private static float calcBezier(float aT, float aA1, float aA2) {
        return (((1.0f - 3.0f * aA2 + 3.0f * aA1) * aT + (3.0f * aA2 - 6.0f * aA1)) * aT + (3.0f * aA1)) * aT;
    }

    // Returns dx/dt given t, x1, and x2, or dy/dt given t, y1, and y2.
    private static float getSlope(float aT, float aA1, float aA2) {
        return 3.0f * (1.0f - 3.0f * aA2 + 3.0f * aA1) * aT * aT + 2.0f * (3.0f * aA2 - 6.0f * aA1) * aT + (3.0f * aA1);
    }

    private static float newtonRaphsonIterate(float aX, float aGuessT, float mX1, float mX2) {
        for (int i = 0; i < newtonIterations; ++i) {
            float currentSlope = getSlope(aGuessT, mX1, mX2);
            if (currentSlope == 0.0f) {
                return aGuessT;
            }
            float currentX = calcBezier(aGuessT, mX1, mX2) - aX;
            aGuessT -= currentX / currentSlope;
        }
        return aGuessT;
    }
}
