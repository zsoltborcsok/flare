package org.nting.flare.java;

public class ActorTriangle extends ActorProceduralPath {
  @override
  void invalidatePath() {}

  @override
  ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorTriangle instance = new ActorTriangle();
    instance.copyPath(this, resetArtboard);
    return instance;
  }

  static ActorTriangle read(ActorArtboard artboard, StreamReader reader,
      ActorTriangle component) {
    component ??= new ActorTriangle();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    return component;
  }

  @override
  List<PathPoint> get points {
    List<PathPoint> _trianglePoints = <PathPoint>[];
    _trianglePoints.add(
        StraightPathPoint.fromTranslation(Vec2D.fromValues(0.0, -radiusY)));
    _trianglePoints.add(
        StraightPathPoint.fromTranslation(Vec2D.fromValues(radiusX, radiusY)));
    _trianglePoints.add(
        StraightPathPoint.fromTranslation(Vec2D.fromValues(-radiusX, radiusY)));

    return _trianglePoints;
  }

  bool get isClosed => true;

  bool get doesDraw => !renderCollapsed;

  double get radiusX => width / 2;

  double get radiusY => height / 2;
}
