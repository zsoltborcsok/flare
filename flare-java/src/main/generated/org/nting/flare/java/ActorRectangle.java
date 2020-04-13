package org.nting.flare.java;

public class ActorRectangle extends ActorProceduralPath {
  double _radius = 0.0;

  @override
  void invalidatePath() {}

  @override
  ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorRectangle instance = new ActorRectangle();
    instance.copyRectangle(this, resetArtboard);
    return instance;
  }

  void copyRectangle(ActorRectangle node, ActorArtboard resetArtboard) {
    copyPath(node, resetArtboard);
    _radius = node._radius;
  }

  static ActorRectangle read(ActorArtboard artboard, StreamReader reader,
      ActorRectangle component) {
    component ??= new ActorRectangle();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    component._radius = reader.readFloat32("cornerRadius");
    return component;
  }

  @override
  List<PathPoint> get points {
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

  set radius(double rd) {
    if (rd != _radius) {
      _radius = rd;
      invalidateDrawable();
    }
  }

  bool get isClosed => true;

  bool get doesDraw {
    return !renderCollapsed;
  }

  double get radius => _radius;
}
