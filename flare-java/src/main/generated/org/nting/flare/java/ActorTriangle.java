package org.nting.flare.java;

import org.nting.flare.java.maths.Vec2D;

import java.util.ArrayList;
import java.util.List;

public class ActorTriangle extends ActorProceduralPath {
  @Override
  public void invalidatePath() {}

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorTriangle instance = new ActorTriangle();
    instance.copyPath(this, resetArtboard);
    return instance;
  }

  static ActorTriangle read(ActorArtboard artboard, StreamReader reader,
      ActorTriangle component) {
    component = component != null ? component : new ActorTriangle();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    return component;
  }

  @Override
  public List<PathPoint> points() {
    List<PathPoint> _trianglePoints = new ArrayList<PathPoint>();
    _trianglePoints.add(
        StraightPathPoint.fromTranslation(Vec2D.fromValues(0.0, -radiusY)));
    _trianglePoints.add(
        StraightPathPoint.fromTranslation(Vec2D.fromValues(radiusX, radiusY)));
    _trianglePoints.add(
        StraightPathPoint.fromTranslation(Vec2D.fromValues(-radiusX, radiusY)));

    return _trianglePoints;
  }

  public boolean isClosed() { return true; }

  public boolean doesDraw() { return !renderCollapsed; }

  public double radiusX() { return width / 2; }

  public double radiusY() { return height / 2; }
}
