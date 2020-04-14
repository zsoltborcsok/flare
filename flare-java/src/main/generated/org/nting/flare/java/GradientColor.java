package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public abstract class GradientColor extends ActorPaint {
  Float32List _colorStops = new Float32List(10);
  final Vec2D _start = new Vec2D();
  final Vec2D _end = new Vec2D();
  final Vec2D _renderStart = new Vec2D();
  final Vec2D _renderEnd = new Vec2D();

  public Vec2D start() { return _start; }

  public Vec2D end() { return _end; }

  public Vec2D renderStart() { return _renderStart; }

  public Vec2D renderEnd() { return _renderEnd; }

  public Float32List colorStops() {
    return _colorStops;
  }

  public void copyGradient(GradientColor node, ActorArtboard resetArtboard) {
    copyPaint(node, resetArtboard);
    _colorStops = Float32List.fromList(node._colorStops);
    Vec2D.copy(_start, node._start);
    Vec2D.copy(_end, node._end);
    opacity = node.opacity;
  }

  static GradientColor read(ActorArtboard artboard, StreamReader reader,
      GradientColor component) {
    ActorPaint.read(artboard, reader, component);

    int numStops = reader.readUint8("numColorStops");
    Float32List stops = reader.readFloat32Array(numStops * 5, "colorStops");
    component._colorStops = stops;

    Vec2D.copyFromList(component._start, reader.readFloat32Array(2, "start"));
    Vec2D.copyFromList(component._end, reader.readFloat32Array(2, "end"));

    return component;
  }

  @Override
  public void onDirty(int dirt) {}

  @Override
  public void update(int dirt) {
    ActorShape shape = parent as ActorShape;
    Mat2D world = shape.worldTransform;
    if (shape.transformAffectsStroke) {
      Vec2D.copy(_renderStart, _start);
      Vec2D.copy(_renderEnd, _end);
    } else {
      Vec2D.transformMat2D(_renderStart, _start, world);
      Vec2D.transformMat2D(_renderEnd, _end, world);
    }
  }
}
