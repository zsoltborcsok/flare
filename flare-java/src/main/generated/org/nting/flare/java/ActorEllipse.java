package org.nting.flare.java;

const double circleConstant = 0.55;

public class ActorEllipse extends ActorProceduralPath {
  @override
  ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorEllipse instance = new ActorEllipse();
    instance.copyPath(this, resetArtboard);
    return instance;
  }

  @override
  void invalidatePath() {}

  static ActorEllipse read(ActorArtboard artboard, StreamReader reader,
      ActorEllipse component) {
    component ??= new ActorEllipse();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    return component;
  }

  @override
  List<PathPoint> get points {
    List<PathPoint> _ellipsePathPoints = <PathPoint>[];
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

  bool get isClosed => true;

  bool get doesDraw {
    return !renderCollapsed;
  }

  double get radiusX => width / 2;

  double get radiusY => height / 2;
}
