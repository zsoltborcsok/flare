package org.nting.flare.java.maths;

public class TransformComponents {
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

  TransformComponents() {
    _buffer = Float32List.fromList([1.0, 0.0, 0.0, 1.0, 0.0, 0.0]);
  }

  TransformComponents.clone(TransformComponents copy) {
    _buffer = Float32List.fromList(copy.values);
  }

  public double x() {
    return _buffer[0];
  }

  set x(double value) {
    _buffer[0] = value;
  }

  public double y() {
    return _buffer[1];
  }

  set y(double value) {
    _buffer[1] = value;
  }

  public double scaleX() {
    return _buffer[2];
  }

  set scaleX(double value) {
    _buffer[2] = value;
  }

  public double scaleY() {
    return _buffer[3];
  }

  set scaleY(double value) {
    _buffer[3] = value;
  }

  public double rotation() {
    return _buffer[4];
  }

  set rotation(double value) {
    _buffer[4] = value;
  }

  public double skew() {
    return _buffer[5];
  }

  set skew(double value) {
    _buffer[5] = value;
  }

  public Vec2D translation() {
    return Vec2D.fromValues(_buffer[0], _buffer[1]);
  }

  public Vec2D scale() {
    return Vec2D.fromValues(_buffer[2], _buffer[3]);
  }
}
