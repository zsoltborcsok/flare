package org.nting.flare.java;

public class ActorPolygon extends ActorProceduralPath {
  int sides = 5;

  @override
  public void invalidatePath() {}

  @override
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
    component ??= new ActorPolygon();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    component.sides = reader.readUint32("sides");
    return component;
  }

  @override
  List<PathPoint> get points {
    List<PathPoint> _polygonPoints = <PathPoint>[];
    double angle = -pi / 2.0;
    double inc = (pi * 2.0) / sides;

    for (int i = 0; i < sides; i++) {
      _polygonPoints.add(StraightPathPoint.fromTranslation(
          Vec2D.fromValues(cos(angle) * radiusX, sin(angle) * radiusY)));
      angle += inc;
    }

    return _polygonPoints;
  }

  bool get isClosed => true;

  bool get doesDraw => !renderCollapsed;

  double get radiusX => width / 2;

  double get radiusY => height / 2;
}
