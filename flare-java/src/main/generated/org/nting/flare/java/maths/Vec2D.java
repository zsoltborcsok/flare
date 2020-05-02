package org.nting.flare.java.maths;

public class Vec2D {
  private float[] _buffer;

  public float[] values() {
    return _buffer;
  }

  double operator [](int index) {
    return _buffer[index];
  }

  void operator []=(int index, double value) {
    _buffer[index] = value;
  }

  Vec2D() {
    _buffer = Float32List.fromList([0.0, 0.0]);
  }

  Vec2D.clone(Vec2D copy) {
    _buffer = Float32List.fromList(copy._buffer);
  }

  Vec2D.fromValues(double x, double y) {
    _buffer = Float32List.fromList([x, y]);
  }

  public static void copy(Vec2D o, Vec2D a) {
    o[0] = a[0];
    o[1] = a[1];
  }

  public static void copyFromList(Vec2D o, float[] a) {
    o[0] = a[0];
    o[1] = a[1];
  }

  public static Vec2D transformMat2D(Vec2D o, Vec2D a, Mat2D m) {
    double x = a[0];
    double y = a[1];
    o[0] = m[0] * x + m[2] * y + m[4];
    o[1] = m[1] * x + m[3] * y + m[5];
    return o;
  }

  public static Vec2D transformMat2(Vec2D o, Vec2D a, Mat2D m) {
    double x = a[0];
    double y = a[1];
    o[0] = m[0] * x + m[2] * y;
    o[1] = m[1] * x + m[3] * y;
    return o;
  }

  public static Vec2D subtract(Vec2D o, Vec2D a, Vec2D b) {
    o[0] = a[0] - b[0];
    o[1] = a[1] - b[1];
    return o;
  }

  public static Vec2D add(Vec2D o, Vec2D a, Vec2D b) {
    o[0] = a[0] + b[0];
    o[1] = a[1] + b[1];
    return o;
  }

  public static Vec2D scale(Vec2D o, Vec2D a, double scale) {
    o[0] = a[0] * scale;
    o[1] = a[1] * scale;
    return o;
  }

  public static Vec2D lerp(Vec2D o, Vec2D a, Vec2D b, double f) {
    double ax = a[0];
    double ay = a[1];
    o[0] = ax + f * (b[0] - ax);
    o[1] = ay + f * (b[1] - ay);
    return o;
  }

  public static double length(Vec2D a) {
    double x = a[0];
    double y = a[1];
    public abstract return sqrt(x * x + y * y);
  }

  public static double squaredLength(Vec2D a) {
    double x = a[0];
    double y = a[1];
    return x * x + y * y;
  }

  public static double distance(Vec2D a, Vec2D b) {
    double x = b[0] - a[0];
    double y = b[1] - a[1];
    public abstract return sqrt(x * x + y * y);
  }

  public static double squaredDistance(Vec2D a, Vec2D b) {
    double x = b[0] - a[0];
    double y = b[1] - a[1];
    return x * x + y * y;
  }

  public static Vec2D negate(Vec2D result, Vec2D a) {
    result[0] = -1 * a[0];
    result[1] = -1 * a[1];

    return result;
  }

  public static void normalize(Vec2D result, Vec2D a) {
    double x = a[0];
    double y = a[1];
    double len = x * x + y * y;
    if (len > 0.0) {
      len = 1.0 / sqrt(len);
      result[0] = a[0] * len;
      result[1] = a[1] * len;
    }
  }

  public static double dot(Vec2D a, Vec2D b) {
    return a[0] * b[0] + a[1] * b[1];
  }

  public static Vec2D scaleAndAdd(Vec2D result, Vec2D a, Vec2D b, double scale) {
    result[0] = a[0] + b[0] * scale;
    result[1] = a[1] + b[1] * scale;
    return result;
  }

  @Override
  public String toString() {
    String v = _buffer[0].toString() + ", ";
    return v + _buffer[1].toString();
  }
}
