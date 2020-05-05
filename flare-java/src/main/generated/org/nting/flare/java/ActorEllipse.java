package org.nting.flare.java;

import org.nting.flare.java.maths.Vec2D;

import java.util.ArrayList;
import java.util.List;

final double circleConstant = 0.55;

public class ActorEllipse extends ActorProceduralPath {
  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorEllipse instance = new ActorEllipse();
    instance.copyPath(this, resetArtboard);
    return instance;
  }

  @Override
  public void invalidatePath() {}

  static ActorEllipse read(ActorArtboard artboard, StreamReader reader,
      ActorEllipse component) {
    component = component != null ? component : new ActorEllipse();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    return component;
  }

  @Override
  public List<PathPoint> points() {
    List<PathPoint> _ellipsePathPoints = new ArrayList<PathPoint>();
    _ellipsePathPoints.add(CubicPathPoint.fromValues(
        Vec2D.fromValues(0.0, -radiusY),
        Vec2D.fromValues(-radiusX * circleConstant, -radiusY),
        Vec2D.fromValues(radiusX * circleConstant, -radiusY)));
    _ellipsePathPoints.add(CubicPathPoint.fromValues(
        Vec2D.fromValues(radiusX, 0.0),
        Vec2D.fromValues(radiusX, circleConstant * -radiusY),
        Vec2D.fromValues(radiusX, circleConstant * radiusY)));
    _ellipsePathPoints.add(CubicPathPoint.fromValues(
        Vec2D.fromValues(0.0, radiusY),
        Vec2D.fromValues(radiusX * circleConstant, radiusY),
        Vec2D.fromValues(-radiusX * circleConstant, radiusY)));
    _ellipsePathPoints.add(CubicPathPoint.fromValues(
        Vec2D.fromValues(-radiusX, 0.0),
        Vec2D.fromValues(-radiusX, radiusY * circleConstant),
        Vec2D.fromValues(-radiusX, -radiusY * circleConstant)));

    return _ellipsePathPoints;
  }

  public boolean isClosed() { return true; }

  public boolean doesDraw() {
    return !renderCollapsed;
  }

  public double radiusX() { return width / 2; }

  public double radiusY() { return height / 2; }
}
