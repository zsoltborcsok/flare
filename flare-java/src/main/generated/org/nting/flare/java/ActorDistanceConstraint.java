package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class ActorDistanceConstraint extends ActorTargetedConstraint {

  public static class DistanceMode {
    public static final int closer = 0;
    public static final int further = 1;
    public static final int exact = 2;
  }

  private double _distance = 100.0;
  private int _mode = DistanceMode.closer;

  ActorDistanceConstraint() : super();

  static ActorDistanceConstraint read(ActorArtboard artboard,
      StreamReader reader, ActorDistanceConstraint component) {
    component = component != null ? component : new ActorDistanceConstraint();
    ActorTargetedConstraint.read(artboard, reader, component);

    component._distance = reader.readFloat32("distance");
    component._mode = reader.readUint8("modeId");

    return component;
  }

  @Override
  public ActorDistanceConstraint makeInstance(ActorArtboard resetArtboard) {
    ActorDistanceConstraint node = new ActorDistanceConstraint();
    node.copyDistanceConstraint(this, resetArtboard);
    return node;
  }

  void copyDistanceConstraint(ActorDistanceConstraint node,
      ActorArtboard resetArtboard) {
    copyTargetedConstraint(node, resetArtboard);
    _distance = node._distance;
    _mode = node._mode;
  }

  @Override
  public void constrain(ActorNode node) {
    ActorNode t = (ActorNode) target;
    if (t == null) {
      return;
    }

    ActorNode p = parent;
    Vec2D targetTranslation = t.getWorldTranslation(Vec2D());
    Vec2D ourTranslation = p.getWorldTranslation(Vec2D());

    Vec2D toTarget = Vec2D.subtract(Vec2D(), ourTranslation, targetTranslation);
    double currentDistance = Vec2D.length(toTarget);
    switch (_mode) {
      case DistanceMode.closer:
        if (currentDistance < _distance) {
          return;
        }
        break;

      case DistanceMode.further:
        if (currentDistance > _distance) {
          return;
        }
        break;
    }

    if (currentDistance < 0.001) {
      return;
    }

    Vec2D.scale(toTarget, toTarget, 1.0 / currentDistance);
    Vec2D.scale(toTarget, toTarget, _distance);

    Mat2D world = p.worldTransform;
    Vec2D position = Vec2D.lerp(Vec2D(), ourTranslation,
        Vec2D.add(Vec2D(), targetTranslation, toTarget), strength);
    world[4] = position[0];
    world[5] = position[1];
  }

  public double distance() { return _distance; }

  public int mode() { return _mode; }

  public void distance(double d) {
    if (_distance != d) {
      _distance = d;
      markDirty();
    }
  }

  public void mode(int m) {
    if (_mode != m) {
      _mode = m;
      markDirty();
    }
  }

  @Override
  public void completeResolve() {}

  @Override
  public void update(int dirt) {}
}
