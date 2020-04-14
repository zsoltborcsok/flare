package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class CubicPathPoint extends PathPoint {
  Vec2D _in = new Vec2D();
  Vec2D _out = new Vec2D();

  CubicPathPoint(PointType type) : super(type);

  Vec2D get inPoint {
    return _in;
  }

  Vec2D get outPoint {
    return _out;
  }

  CubicPathPoint.fromValues(Vec2D translation, Vec2D inPoint, Vec2D outPoint)
      : super(PointType.disconnected) {
    _translation = translation;
    _in = inPoint;
    _out = outPoint;
  }

  @Override
  public PathPoint makeInstance() {
    CubicPathPoint node = new CubicPathPoint(_type);
    node.copyCubic(this);
    return node;
  }

  public void copyCubic(CubicPathPoint from) {
    super.copy(from);
    Vec2D.copy(_in, from._in);
    Vec2D.copy(_out, from._out);
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
    CubicPathPoint result = super.transformed(transform) as CubicPathPoint;
    Vec2D.transformMat2D(result.inPoint, result.inPoint, transform);
    Vec2D.transformMat2D(result.outPoint, result.outPoint, transform);
    return result;
  }

  @Override
  public PathPoint skin(Mat2D world, Float32List bones) {
    CubicPathPoint point = new CubicPathPoint(pointType);

    double px =
        world[0] * translation[0] + world[2] * translation[1] + world[4];
    double py =
        world[1] * translation[0] + world[3] * translation[1] + world[5];

    {
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
    }

    {
      double a = 0.0,
          b = 0.0,
          c = 0.0,
          d = 0.0,
          e = 0.0,
          f = 0.0;
      px = world[0] * _in[0] + world[2] * _in[1] + world[4];
      py = world[1] * _in[0] + world[3] * _in[1] + world[5];

      for (int i = 8; i < 12; i++) {
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

      Vec2D pos = point.inPoint;
      pos[0] = a * px + c * py + e;
      pos[1] = b * px + d * py + f;
    }

    {
      double a = 0.0,
          b = 0.0,
          c = 0.0,
          d = 0.0,
          e = 0.0,
          f = 0.0;
      px = world[0] * _out[0] + world[2] * _out[1] + world[4];
      py = world[1] * _out[0] + world[3] * _out[1] + world[5];

      for (int i = 16; i < 20; i++) {
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

      Vec2D pos = point.outPoint;
      pos[0] = a * px + c * py + e;
      pos[1] = b * px + d * py + f;
    }

    return point;
  }
}