package org.nting.flare.java;

public class ActorStar extends ActorProceduralPath {
  int _numPoints = 5;
  double _innerRadius = 0.0;

  @override
  public void invalidatePath() {}

  @override
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
    component ??= new ActorStar();

    ActorNode.read(artboard, reader, component);

    component.width = reader.readFloat32("width");
    component.height = reader.readFloat32("height");
    component._numPoints = reader.readUint32("points");
    component._innerRadius = reader.readFloat32("innerRadius");
    return component;
  }

  @override
  List<PathPoint> get points {
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

  set innerRadius(double val) {
    if (val != _innerRadius) {
      _innerRadius = val;
      invalidateDrawable();
    }
  }

  double get innerRadius => _innerRadius;

  bool get isClosed => true;

  bool get doesDraw => !renderCollapsed;

  double get radiusX => width / 2;

  double get radiusY => height / 2;

  int get numPoints => _numPoints;

  int get sides => _numPoints * 2;
}
