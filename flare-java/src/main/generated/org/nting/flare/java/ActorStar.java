package org.nting.flare.java;

import org.nting.flare.java.maths.Vec2D;

import java.util.List;

public class ActorStar extends ActorProceduralPath {
  private int _numPoints = 5;
  private double _innerRadius = 0.0;

  @Override
  public void invalidatePath() {}

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorStar instance = new ActorStar();
    instance.copyStar(this, resetArtboard);
    return instance;
  }

  public void copyStar(ActorStar node, ActorArtboard resetArtboard) {
    copyPath(node, resetArtboard);
    _numPoints = node._numPoints;
    _innerRadius = node._innerRadius;
  }

  static ActorStar read(ActorArtboard artboard, StreamReader reader,
      ActorStar component) {
    component = component != null ? component : new ActorStar();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    component._numPoints = reader.readUint32("points");
    component._innerRadius = reader.readFloat32("innerRadius");
    return component;
  }

  @Override
  public List<PathPoint> points() {
    List<PathPoint> _starPoints = <PathPoint>[
      StraightPathPoint.fromTranslation(Vec2D.fromValues(0.0, -radiusY))
    ];

    double angle = -pi / 2.0;
    double inc = (pi * 2.0) / sides;
    Vec2D sx = Vec2D.fromValues(radiusX, radiusX * _innerRadius);
    Vec2D sy = Vec2D.fromValues(radiusY, radiusY * _innerRadius);

    for (int i = 0; i < sides; i++) {
      _starPoints.add(StraightPathPoint.fromTranslation(
          Vec2D.fromValues(cos(angle) * sx[i % 2], sin(angle) * sy[i % 2])));
      angle += inc;
    }
    return _starPoints;
  }

  public void innerRadius(double val) {
    if (val != _innerRadius) {
      _innerRadius = val;
      invalidateDrawable();
    }
  }

  public double innerRadius() { return _innerRadius; }

  public boolean isClosed() { return true; }

  public boolean doesDraw() { return !renderCollapsed; }

  public double radiusX() { return width / 2; }

  public double radiusY() { return height / 2; }

  public int numPoints() { return _numPoints; }

  public int sides() { return _numPoints * 2; }
}
