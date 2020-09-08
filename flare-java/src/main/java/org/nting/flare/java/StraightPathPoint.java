package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class StraightPathPoint extends PathPoint {

    public float radius = 0.0f;

    public StraightPathPoint() {
        super(PointType.straight);
    }

    public StraightPathPoint(Vec2D translation) {
        super(PointType.straight);
        _translation = translation;
    }

    public StraightPathPoint(Vec2D translation, float radius) {
        super(PointType.straight);
        _translation = translation;
        this.radius = radius;
    }

    public StraightPathPoint(StraightPathPoint from) {
        super(from);
        radius = from.radius;
    }

    @Override
    public PathPoint makeInstance() {
        return new StraightPathPoint(this);
    }

    @Override
    public int readPoint(StreamReader reader, boolean isConnectedToBones) {
        radius = reader.readFloat32("radius");
        if (isConnectedToBones) {
            return 8;
        }
        return 0;
    }

    @Override
    public PathPoint skin(Mat2D world, float[] bones) {
        StraightPathPoint point = new StraightPathPoint();
        point.radius = radius; // Cascade notation (..) was used

        float px = world.values()[0] * translation().values()[0] + world.values()[2] * translation().values()[1]
                + world.values()[4];
        float py = world.values()[1] * translation().values()[0] + world.values()[3] * translation().values()[1]
                + world.values()[5];

        float a = 0.0f, b = 0.0f, c = 0.0f, d = 0.0f, e = 0.0f, f = 0.0f;

        for (int i = 0; i < 4; i++) {
            int boneIndex = (int) Math.floor(_weights[i]);
            float weight = _weights[i + 4];
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

        return point;
    }
}
