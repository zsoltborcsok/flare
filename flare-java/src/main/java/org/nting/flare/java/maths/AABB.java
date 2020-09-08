package org.nting.flare.java.maths;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;

public class AABB {

    private final float[] _buffer;

    public AABB() {
        _buffer = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
    }

    public AABB(AABB copy) {
        _buffer = Arrays.copyOf(copy._buffer, copy._buffer.length);
    }

    public AABB(float a, float b, float c, float d) {
        _buffer = new float[] { a, b, c, d };
    }

    public float[] values() {
        return _buffer;
    }

    public Vec2D minimum() {
        return new Vec2D(_buffer[0], _buffer[1]);
    }

    public Vec2D maximum() {
        return new Vec2D(_buffer[2], _buffer[3]);
    }

    public static AABB copy(AABB out, AABB a) {
        out.values()[0] = a.values()[0];
        out.values()[1] = a.values()[1];
        out.values()[2] = a.values()[2];
        out.values()[3] = a.values()[3];
        return out;
    }

    public static Vec2D center(Vec2D out, AABB a) {
        out.values()[0] = (a.values()[0] + a.values()[2]) * 0.5f;
        out.values()[1] = (a.values()[1] + a.values()[3]) * 0.5f;
        return out;
    }

    public static Vec2D size(Vec2D out, AABB a) {
        out.values()[0] = a.values()[2] - a.values()[0];
        out.values()[1] = a.values()[3] - a.values()[1];
        return out;
    }

    public static Vec2D extents(Vec2D out, AABB a) {
        out.values()[0] = (a.values()[2] - a.values()[0]) * 0.5f;
        out.values()[1] = (a.values()[3] - a.values()[1]) * 0.5f;
        return out;
    }

    public static float perimeter(AABB a) {
        float wx = a.values()[2] - a.values()[0];
        float wy = a.values()[3] - a.values()[1];
        return 2.0f * (wx + wy);
    }

    public static AABB combine(AABB out, AABB a, AABB b) {
        out.values()[0] = min(a.values()[0], b.values()[0]);
        out.values()[1] = min(a.values()[1], b.values()[1]);
        out.values()[2] = max(a.values()[2], b.values()[2]);
        out.values()[3] = max(a.values()[3], b.values()[3]);
        return out;
    }

    public static boolean contains(AABB a, AABB b) {
        return a.values()[0] <= b.values()[0] && a.values()[1] <= b.values()[1] && b.values()[2] <= a.values()[2]
                && b.values()[3] <= a.values()[3];
    }

    public static boolean isValid(AABB a) {
        float dx = a.values()[2] - a.values()[0];
        float dy = a.values()[3] - a.values()[1];
        return dx >= 0 && dy >= 0 && a.values()[0] <= Float.MAX_VALUE && a.values()[1] <= Float.MAX_VALUE
                && a.values()[2] <= Float.MAX_VALUE && a.values()[3] <= Float.MAX_VALUE;
    }

    public static boolean testOverlap(AABB a, AABB b) {
        float d1x = b.values()[0] - a.values()[2];
        float d1y = b.values()[1] - a.values()[3];

        float d2x = a.values()[0] - b.values()[2];
        float d2y = a.values()[1] - b.values()[3];

        if (d1x > 0.0 || d1y > 0.0) {
            return false;
        }

        if (d2x > 0.0 || d2y > 0.0) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(_buffer);
    }
}
