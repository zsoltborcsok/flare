package org.nting.flare.java;

import static java.lang.Math.min;
import org.nting.flare.java.maths.Vec2D;

import java.util.List;

public class ActorRectangle extends ActorProceduralPath {
  private double _radius = 0.0;

  @Override
  public void invalidatePath() {}

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorRectangle instance = new ActorRectangle();
    instance.copyRectangle(this, resetArtboard);
    return instance;
  }

  public void copyRectangle(ActorRectangle node, ActorArtboard resetArtboard) {
    copyPath(node, resetArtboard);
    _radius = node._radius;
  }

  static ActorRectangle read(ActorArtboard artboard, StreamReader reader,
      ActorRectangle component) {
    component = component != null ? component : new ActorRectangle();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    component._radius = reader.readFloat32("cornerRadius");
    return component;
  }

  @Override
  public List<PathPoint> points() {
    double halfWidth = width / 2;
    double halfHeight = height / 2;
    double renderRadius = min(_radius, min(halfWidth, halfHeight));
    List<PathPoint> _rectanglePathPoints = <PathPoint>[];
    _rectanglePathPoints.add(StraightPathPoint.fromValues(
        Vec2D.fromValues(-halfWidth, -halfHeight), renderRadius));
    _rectanglePathPoints.add(StraightPathPoint.fromValues(
        Vec2D.fromValues(halfWidth, -halfHeight), renderRadius));
    _rectanglePathPoints.add(StraightPathPoint.fromValues(
        Vec2D.fromValues(halfWidth, halfHeight), renderRadius));
    _rectanglePathPoints.add(StraightPathPoint.fromValues(
        Vec2D.fromValues(-halfWidth, halfHeight), renderRadius));

    return _rectanglePathPoints;
  }

  public void radius(double rd) {
    if (rd != _radius) {
      _radius = rd;
      invalidateDrawable();
    }
  }

  public boolean isClosed() { return true; }

  public boolean doesDraw() {
    return !renderCollapsed;
  }

  public double radius() { return _radius; }
}
