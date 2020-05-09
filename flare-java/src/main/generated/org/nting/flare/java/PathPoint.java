package org.nting.flare.java;

import java.util.Arrays;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public abstract class PathPoint {

    protected final PointType _type;
    protected Vec2D _translation = new Vec2D();
    protected float[] _weights;

    public PathPoint(PointType type) {
        _type = type;
    }

    public PathPoint(PathPoint from) {
        _type = from._type;
        Vec2D.copy(_translation, from._translation);
        if (from._weights != null) {
            _weights = Arrays.copyOf(from._weights, from._weights.length);
        }
    }

    public PointType pointType() {
        return _type;
    }

    public Vec2D translation() {
        return _translation;
    }

    public abstract PathPoint makeInstance();

    public void read(StreamReader reader, boolean isConnectedToBones) {
        Vec2D.copyFromList(_translation, reader.readFloat32Array(2, "translation"));

        int weightLength = readPoint(reader, isConnectedToBones);
        if (weightLength != 0) {
            _weights = reader.readFloat32Array(weightLength, "weights");
        }
    }

    public abstract int readPoint(StreamReader reader, boolean isConnectedToBones);

    public PathPoint transformed(Mat2D transform) {
        PathPoint result = makeInstance();
        Vec2D.transformMat2D(result.translation(), result.translation(), transform);
        return result;
    }

    public abstract PathPoint skin(Mat2D world, float[] bones);
}
