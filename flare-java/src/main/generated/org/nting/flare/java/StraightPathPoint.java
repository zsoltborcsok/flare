package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class StraightPathPoint extends PathPoint {
  double radius = 0.0;

  StraightPathPoint() : super(PointType.straight);

  StraightPathPoint.fromTranslation(Vec2D translation)
      : super(PointType.straight) {
    _translation = translation;
  }

  StraightPathPoint.fromValues(Vec2D translation, this.radius)
      : super(PointType.straight) {
    _translation = translation;
  }

  @Override
  public PathPoint makeInstance() {
    StraightPathPoint node = new StraightPathPoint();
    node.copyStraight(this);
    return node;
  }

  public void copyStraight(StraightPathPoint from) {
    super.copy(from);
    radius = from.radius;
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
    StraightPathPoint point = new StraightPathPoint()
      ..radius = radius;

    double px =
        world[0] * translation[0] + world[2] * translation[1] + world[4];
    double py =
        world[1] * translation[0] + world[3] * translation[1] + world[5];

    double a = 0.0,
        b = 0.0,
        c = 0.0,
        d = 0.0,
        e = 0.0,
        f = 0.0;

    for (int i = 0; i < 4; i++) {
      int boneIndex = _weights[i].floor();
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

    Vec2D pos = point.translation;
    pos[0] = a * px + c * py + e;
    pos[1] = b * px + d * py + f;

    return point;
  }
}
