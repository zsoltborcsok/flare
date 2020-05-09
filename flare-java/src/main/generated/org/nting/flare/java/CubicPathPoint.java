package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class CubicPathPoint extends PathPoint {

    private Vec2D _in = new Vec2D();
    private Vec2D _out = new Vec2D();

    public CubicPathPoint(PointType type) {
        super(type);
    }

    public CubicPathPoint(Vec2D translation, Vec2D inPoint, Vec2D outPoint) {
        super(PointType.disconnected);
        _translation = translation;
        _in = inPoint;
        _out = outPoint;
    }

    public CubicPathPoint(CubicPathPoint from) {
        super(from);
        Vec2D.copy(_in, from._in);
        Vec2D.copy(_out, from._out);
    }

    public Vec2D inPoint() {
        return _in;
    }

    public Vec2D outPoint() {
        return _out;
    }

    @Override
    public PathPoint makeInstance() {
        return new CubicPathPoint(this);
    }

    @Override
    public int readPoint(StreamReader reader, boolean isConnectedToBones) {
        Vec2D.copyFromList(_in, reader.readFloat32Array(2, "in"));
        Vec2D.copyFromList(_out, reader.readFloat32Array(2, "out"));
        if (isConnectedToBones) {
            return 24;
        }
        return 0;
    }

    @Override
    public PathPoint transformed(Mat2D transform) {
        CubicPathPoint result = (CubicPathPoint) super.transformed(transform);
        Vec2D.transformMat2D(result.inPoint(), result.inPoint(), transform);
        Vec2D.transformMat2D(result.outPoint(), result.outPoint(), transform);
        return result;
    }

    @Override
    public PathPoint skin(Mat2D world, float[] bones) {
        CubicPathPoint point = new CubicPathPoint(pointType());

        float px = world.values()[0] * translation().values()[0] + world.values()[2] * translation().values()[1]
                + world.values()[4];
        float py = world.values()[1] * translation().values()[0] + world.values()[3] * translation().values()[1]
                + world.values()[5];

        {
            float a = 0.0f, b = 0.0f, c = 0.0f, d = 0.0f, e = 0.0f, f = 0.0f;

            for (int i = 0; i < 4; i++) {
                int boneIndex = (int) Math.floor(_weights[i]);
                double weight = _weights[i + 4];
                if (weight > 0) {
                    int bb = boneIndex * 6;

                    a += bones[bb] * weight;
                    b += bones[bb + 1] * weight;
                    c += bones[bb + 2] * weight;
                    d += bones[bb + 3] * weight;
                    e += bones[bb + 4] * weight;
                    f += bones[bb + 5] * weight;
                }
            }

            Vec2D pos = point.translation();
            pos.values()[0] = a * px + c * py + e;
            pos.values()[1] = b * px + d * py + f;
        }

        {
            float a = 0.0f, b = 0.0f, c = 0.0f, d = 0.0f, e = 0.0f, f = 0.0f;
            px = world.values()[0] * _in.values()[0] + world.values()[2] * _in.values()[1] + world.values()[4];
            py = world.values()[1] * _in.values()[0] + world.values()[3] * _in.values()[1] + world.values()[5];

            for (int i = 8; i < 12; i++) {
                int boneIndex = (int) Math.floor(_weights[i]);
                double weight = _weights[i + 4];
                if (weight > 0) {
                    int bb = boneIndex * 6;

                    a += bones[bb] * weight;
                    b += bones[bb + 1] * weight;
                    c += bones[bb + 2] * weight;
                    d += bones[bb + 3] * weight;
                    e += bones[bb + 4] * weight;
                    f += bones[bb + 5] * weight;
                }
            }

            Vec2D pos = point.inPoint();
            pos.values()[0] = a * px + c * py + e;
            pos.values()[1] = b * px + d * py + f;
        }

        {
            float a = 0.0f, b = 0.0f, c = 0.0f, d = 0.0f, e = 0.0f, f = 0.0f;
            px = world.values()[0] * _out.values()[0] + world.values()[2] * _out.values()[1] + world.values()[4];
            py = world.values()[1] * _out.values()[0] + world.values()[3] * _out.values()[1] + world.values()[5];

            for (int i = 16; i < 20; i++) {
                int boneIndex = (int) Math.floor(_weights[i]);
                double weight = _weights[i + 4];
                if (weight > 0) {
                    int bb = boneIndex * 6;

                    a += bones[bb] * weight;
                    b += bones[bb + 1] * weight;
                    c += bones[bb + 2] * weight;
                    d += bones[bb + 3] * weight;
                    e += bones[bb + 4] * weight;
                    f += bones[bb + 5] * weight;
                }
            }

            Vec2D pos = point.outPoint();
            pos.values()[0] = a * px + c * py + e;
            pos.values()[1] = b * px + d * py + f;
        }

        return point;
    }
}
