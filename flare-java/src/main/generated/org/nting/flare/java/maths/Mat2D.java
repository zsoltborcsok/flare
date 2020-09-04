package org.nting.flare.java.maths;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.Arrays;

public class Mat2D {

    private final float[] _buffer;

    public Mat2D() {
        _buffer = new float[] { 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f };
    }

    public Mat2D(Mat2D copy) {
        _buffer = Arrays.copyOf(copy._buffer, copy._buffer.length);
    }

    public float[] values() {
        return _buffer;
    }

    public float[] mat4() {
        return new float[] { _buffer[0], _buffer[1], 0.0f, 0.0f, _buffer[2], _buffer[3], 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, _buffer[4], _buffer[5], 0.0f, 1.0f };
    }

    public static void fromRotation(Mat2D o, float rad) {
        float s = (float) sin(rad);
        float c = (float) cos(rad);
        o.values()[0] = c;
        o.values()[1] = s;
        o.values()[2] = -s;
        o.values()[3] = c;
        o.values()[4] = 0.0f;
        o.values()[5] = 0.0f;
    }

    public static void copy(Mat2D o, Mat2D f) {
        o.values()[0] = f.values()[0];
        o.values()[1] = f.values()[1];
        o.values()[2] = f.values()[2];
        o.values()[3] = f.values()[3];
        o.values()[4] = f.values()[4];
        o.values()[5] = f.values()[5];
    }

    public static void copyFromList(Mat2D o, float[] f) {
        o.values()[0] = f[0];
        o.values()[1] = f[1];
        o.values()[2] = f[2];
        o.values()[3] = f[3];
        o.values()[4] = f[4];
        o.values()[5] = f[5];
    }

    public static void scale(Mat2D o, Mat2D a, Vec2D v) {
        float a0 = a.values()[0], a1 = a.values()[1], a2 = a.values()[2], a3 = a.values()[3], a4 = a.values()[4],
                a5 = a.values()[5], v0 = v.values()[0], v1 = v.values()[1];
        o.values()[0] = a0 * v0;
        o.values()[1] = a1 * v0;
        o.values()[2] = a2 * v1;
        o.values()[3] = a3 * v1;
        o.values()[4] = a4;
        o.values()[5] = a5;
    }

    public static void multiply(Mat2D o, Mat2D a, Mat2D b) {
        float a0 = a.values()[0], a1 = a.values()[1], a2 = a.values()[2], a3 = a.values()[3], a4 = a.values()[4],
                a5 = a.values()[5], b0 = b.values()[0], b1 = b.values()[1], b2 = b.values()[2], b3 = b.values()[3],
                b4 = b.values()[4], b5 = b.values()[5];
        o.values()[0] = a0 * b0 + a2 * b1;
        o.values()[1] = a1 * b0 + a3 * b1;
        o.values()[2] = a0 * b2 + a2 * b3;
        o.values()[3] = a1 * b2 + a3 * b3;
        o.values()[4] = a0 * b4 + a2 * b5 + a4;
        o.values()[5] = a1 * b4 + a3 * b5 + a5;
    }

    public static void cCopy(Mat2D o, Mat2D a) {
        o.values()[0] = a.values()[0];
        o.values()[1] = a.values()[1];
        o.values()[2] = a.values()[2];
        o.values()[3] = a.values()[3];
        o.values()[4] = a.values()[4];
        o.values()[5] = a.values()[5];
    }

    public static boolean invert(Mat2D o, Mat2D a) {
        float aa = a.values()[0], ab = a.values()[1], ac = a.values()[2], ad = a.values()[3], atx = a.values()[4],
                aty = a.values()[5];

        float det = aa * ad - ab * ac;
        if (det == 0.0) {
            return false;
        }
        det = 1.0f / det;

        o.values()[0] = ad * det;
        o.values()[1] = -ab * det;
        o.values()[2] = -ac * det;
        o.values()[3] = aa * det;
        o.values()[4] = (ac * aty - ad * atx) * det;
        o.values()[5] = (ab * atx - aa * aty) * det;
        return true;
    }

    public static void getScale(Mat2D m, Vec2D s) {
        float x = m.values()[0];
        float y = m.values()[1];
        s.values()[0] = (float) (signum(x) * sqrt(x * x + y * y));

        x = m.values()[2];
        y = m.values()[3];
        s.values()[1] = (float) (signum(y) * sqrt(x * x + y * y));
    }

    public static void identity(Mat2D mat) {
        mat.values()[0] = 1.0f;
        mat.values()[1] = 0.0f;
        mat.values()[2] = 0.0f;
        mat.values()[3] = 1.0f;
        mat.values()[4] = 0.0f;
        mat.values()[5] = 0.0f;
    }

    public static void decompose(Mat2D m, TransformComponents result) {
        float m0 = m.values()[0], m1 = m.values()[1], m2 = m.values()[2], m3 = m.values()[3];

        float rotation = (float) atan2(m1, m0);
        float denom = m0 * m0 + m1 * m1;
        float scaleX = (float) sqrt(denom);
        float scaleY = (scaleX == 0) ? 0 : ((m0 * m3 - m2 * m1) / scaleX);
        float skewX = (float) atan2(m0 * m2 + m1 * m3, denom);

        result.values()[0] = m.values()[4];
        result.values()[1] = m.values()[5];
        result.values()[2] = scaleX;
        result.values()[3] = scaleY;
        result.values()[4] = rotation;
        result.values()[5] = skewX;
    }

    public static void compose(Mat2D m, TransformComponents result) {
        float r = result.values()[4];

        if (r != 0.0) {
            Mat2D.fromRotation(m, r);
        } else {
            Mat2D.identity(m);
        }
        m.values()[4] = result.values()[0];
        m.values()[5] = result.values()[1];
        Mat2D.scale(m, m, result.scale());

        float sk = result.values()[5];
        if (sk != 0.0) {
            m.values()[2] = m.values()[0] * sk + m.values()[2];
            m.values()[3] = m.values()[1] * sk + m.values()[3];
        }
    }

    public static boolean areEqual(Mat2D a, Mat2D b) {
        return a.values()[0] == b.values()[0] && a.values()[1] == b.values()[1] && a.values()[2] == b.values()[2]
                && a.values()[3] == b.values()[3] && a.values()[4] == b.values()[4] && a.values()[5] == b.values()[5];
    }

    @Override
    public String toString() {
        return Arrays.toString(_buffer);
    }
}
