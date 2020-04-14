package org.nting.flare.java;

public abstract class PathPoint {
  PointType _type;
  Vec2D _translation = new Vec2D();
  Float32List _weights;

  PathPoint(PointType type) {
    _type = type;
  }

  PointType get pointType {
    return _type;
  }

  Vec2D get translation {
    return _translation;
  }

  public abstract PathPoint makeInstance();

  public void copy(PathPoint from) {
    _type = from._type;
    Vec2D.copy(_translation, from._translation);
    if (from._weights != null) {
      _weights = Float32List.fromList(from._weights);
    }
  }

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
    Vec2D.transformMat2D(result.translation, result.translation, transform);
    return result;
  }

  public abstract PathPoint skin(Mat2D world, Float32List bones);
}

