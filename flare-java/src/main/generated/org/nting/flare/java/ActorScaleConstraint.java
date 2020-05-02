package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.TransformComponents;

public class ActorScaleConstraint extends ActorAxisConstraint {
  final TransformComponents _componentsA = new TransformComponents();
  final TransformComponents _componentsB = new TransformComponents();

  ActorScaleConstraint() : super();

  static ActorScaleConstraint read(ActorArtboard artboard, StreamReader reader,
      ActorScaleConstraint component) {
    component = component != null ? component : new ActorScaleConstraint();
    ActorAxisConstraint.read(artboard, reader, component);
    return component;
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorScaleConstraint node = new ActorScaleConstraint();
    node.copyAxisConstraint(this, resetArtboard);
    return node;
  }

  @Override
  public void constrain(ActorNode node) {
    ActorNode t = (ActorNode) target;
    ActorNode p = parent;
    ActorNode grandParent = p.parent;

    Mat2D transformA = parent.worldTransform;
    Mat2D transformB = new Mat2D();
    Mat2D.decompose(transformA, _componentsA);
    if (t == null) {
      Mat2D.copy(transformB, transformA);
      _componentsB[0] = _componentsA[0];
      _componentsB[1] = _componentsA[1];
      _componentsB[2] = _componentsA[2];
      _componentsB[3] = _componentsA[3];
      _componentsB[4] = _componentsA[4];
      _componentsB[5] = _componentsA[5];
    } else {
      Mat2D.copy(transformB, t.worldTransform);
      if (sourceSpace == TransformSpace.local) {
        ActorNode sourceGrandParent = t.parent;
        if (sourceGrandParent != null) {
          Mat2D inverse = new Mat2D();
          Mat2D.invert(inverse, sourceGrandParent.worldTransform);
          Mat2D.multiply(transformB, inverse, transformB);
        }
      }
      Mat2D.decompose(transformB, _componentsB);

      if (!copyX) {
        _componentsB[2] =
        destSpace == TransformSpace.local ? 1.0 : _componentsA[2];
      } else {
        _componentsB[2] *= scaleX;
        if (offset) {
          _componentsB[2] *= parent.scaleX;
        }
      }

      if (!copyY) {
        _componentsB[3] =
        destSpace == TransformSpace.local ? 0.0 : _componentsA[3];
      } else {
        _componentsB[3] *= scaleY;

        if (offset) {
          _componentsB[3] *= parent.scaleY;
        }
      }

      if (destSpace == TransformSpace.local) {
        // Destination space is in parent transform coordinates.
        // Recompose the parent local transform and get it in world,
        // then decompose the world for interpolation.
        if (grandParent != null) {
          Mat2D.compose(transformB, _componentsB);
          Mat2D.multiply(transformB, grandParent.worldTransform, transformB);
          Mat2D.decompose(transformB, _componentsB);
        }
      }
    }

    boolean clampLocal =
        minMaxSpace == TransformSpace.local && grandParent != null;
    if (clampLocal) {
      // Apply min max in local space, so transform to local coordinates first.
      Mat2D.compose(transformB, _componentsB);
      Mat2D inverse = new Mat2D();
      Mat2D.invert(inverse, grandParent.worldTransform);
      Mat2D.multiply(transformB, inverse, transformB);
      Mat2D.decompose(transformB, _componentsB);
    }
    if (enableMaxX && _componentsB[2] > maxX) {
      _componentsB[2] = maxX;
    }
    if (enableMinX && _componentsB[2] < minX) {
      _componentsB[2] = minX;
    }
    if (enableMaxY && _componentsB[3] > maxY) {
      _componentsB[3] = maxY;
    }
    if (enableMinY && _componentsB[3] < minY) {
      _componentsB[3] = minY;
    }
    if (clampLocal) {
      // Transform back to world.
      Mat2D.compose(transformB, _componentsB);
      Mat2D.multiply(transformB, grandParent.worldTransform, transformB);
      Mat2D.decompose(transformB, _componentsB);
    }

    double ti = 1.0 - strength;

    _componentsB[4] = _componentsA[4];
    _componentsB[0] = _componentsA[0];
    _componentsB[1] = _componentsA[1];
    _componentsB[2] = _componentsA[2] * ti + _componentsB[2] * strength;
    _componentsB[3] = _componentsA[3] * ti + _componentsB[3] * strength;
    _componentsB[5] = _componentsA[5];

    Mat2D.compose(parent.worldTransform, _componentsB);
  }

  @Override
  public void update(int dirt) {}

  @Override
  public void completeResolve() {}
}
