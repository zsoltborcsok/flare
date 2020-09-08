package org.nting.flare.java.maths;

import static java.lang.Math.sqrt;

import java.util.Arrays;

public class Vec2D {

    private final float[] _buffer;

    public Vec2D() {
        _buffer = new float[] { 0.0f, 0.0f };
    }

    public Vec2D(Vec2D copy) {
        _buffer = Arrays.copyOf(copy._buffer, copy._buffer.length);
    }

    public Vec2D(float x, float y) {
        _buffer = new float[] { x, y };
    }

    public float[] values() {
        return _buffer;
    }

    public static void copy(Vec2D o, Vec2D a) {
        o.values()[0] = a.values()[0];
        o.values()[1] = a.values()[1];
    }

    public static void copyFromList(Vec2D o, float[] a) {
        o.values()[0] = a[0];
        o.values()[1] = a[1];
    }

    public static Vec2D transformMat2D(Vec2D o, Vec2D a, Mat2D m) {
        float x = a.values()[0];
        float y = a.values()[1];
        o.values()[0] = m.values()[0] * x + m.values()[2] * y + m.values()[4];
        o.values()[1] = m.values()[1] * x + m.values()[3] * y + m.values()[5];
        return o;
    }

    public static Vec2D transformMat2(Vec2D o, Vec2D a, Mat2D m) {
        float x = a.values()[0];
        float y = a.values()[1];
        o.values()[0] = m.values()[0] * x + m.values()[2] * y;
        o.values()[1] = m.values()[1] * x + m.values()[3] * y;
        return o;
    }

    public static Vec2D subtract(Vec2D o, Vec2D a, Vec2D b) {
        o.values()[0] = a.values()[0] - b.values()[0];
        o.values()[1] = a.values()[1] - b.values()[1];
        return o;
    }

    public static Vec2D add(Vec2D o, Vec2D a, Vec2D b) {
        o.values()[0] = a.values()[0] + b.values()[0];
        o.values()[1] = a.values()[1] + b.values()[1];
        return o;
    }

    public static Vec2D scale(Vec2D o, Vec2D a, float scale) {
        o.values()[0] = a.values()[0] * scale;
        o.values()[1] = a.values()[1] * scale;
        return o;
    }

    public static Vec2D lerp(Vec2D o, Vec2D a, Vec2D b, float f) {
        float ax = a.values()[0];
        float ay = a.values()[1];
        o.values()[0] = ax + f * (b.values()[0] - ax);
        o.values()[1] = ay + f * (b.values()[1] - ay);
        return o;
    }

    public static float length(Vec2D a) {
        float x = a.values()[0];
        float y = a.values()[1];
        return (float) sqrt(x * x + y * y);
    }

    public static float squaredLength(Vec2D a) {
        float x = a.values()[0];
        float y = a.values()[1];
        return x * x + y * y;
    }

    public static float distance(Vec2D a, Vec2D b) {
        float x = b.values()[0] - a.values()[0];
        float y = b.values()[1] - a.values()[1];
        return (float) sqrt(x * x + y * y);
    }

    public static float squaredDistance(Vec2D a, Vec2D b) {
        float x = b.values()[0] - a.values()[0];
        float y = b.values()[1] - a.values()[1];
        return x * x + y * y;
    }

    public static Vec2D negate(Vec2D result, Vec2D a) {
        result.values()[0] = -1 * a.values()[0];
        result.values()[1] = -1 * a.values()[1];

        return result;
    }

    public static void normalize(Vec2D result, Vec2D a) {
        float x = a.values()[0];
        float y = a.values()[1];
        float len = x * x + y * y;
        if (len > 0.0) {
            len = 1.0f / (float) sqrt(len);
            result.values()[0] = a.values()[0] * len;
            result.values()[1] = a.values()[1] * len;
        }
    }

    public static float dot(Vec2D a, Vec2D b) {
        return a.values()[0] * b.values()[0] + a.values()[1] * b.values()[1];
    }

    public static Vec2D scaleAndAdd(Vec2D result, Vec2D a, Vec2D b, float scale) {
        result.values()[0] = a.values()[0] + b.values()[0] * scale;
        result.values()[1] = a.values()[1] + b.values()[1] * scale;
        return result;
    }

    @Override
    public String toString() {
        String v = _buffer[0] + ", ";
        return v + _buffer[1];
    }
}
