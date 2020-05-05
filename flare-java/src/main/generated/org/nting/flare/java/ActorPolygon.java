package org.nting.flare.java;

import org.nting.flare.java.maths.Vec2D;

import java.util.ArrayList;
import java.util.List;

public class ActorPolygon extends ActorProceduralPath {
  public int sides = 5;

  @Override
  public void invalidatePath() {}

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorPolygon instance = new ActorPolygon();
    instance.copyPolygon(this, resetArtboard);
    return instance;
  }

  public void copyPolygon(ActorPolygon node, ActorArtboard resetArtboard) {
    copyPath(node, resetArtboard);
    sides = node.sides;
  }

  static ActorPolygon read(ActorArtboard artboard, StreamReader reader,
      ActorPolygon component) {
    component = component != null ? component : new ActorPolygon();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    component.sides = reader.readUint32("sides");
    return component;
  }

  @Override
  public List<PathPoint> points() {
    List<PathPoint> _polygonPoints = new ArrayList<PathPoint>();
    double angle = -pi / 2.0;
    double inc = (pi * 2.0) / sides;

    for (int i = 0; i < sides; i++) {
      _polygonPoints.add(StraightPathPoint.fromTranslation(
          Vec2D.fromValues(cos(angle) * radiusX, sin(angle) * radiusY)));
      angle += inc;
    }

    return _polygonPoints;
  }

  public boolean isClosed() { return true; }

  public boolean doesDraw() { return !renderCollapsed; }

  public double radiusX() { return width / 2; }

  public double radiusY() { return height / 2; }
}
