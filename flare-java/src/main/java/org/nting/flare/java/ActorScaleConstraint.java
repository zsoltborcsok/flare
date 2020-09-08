package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.TransformComponents;

public class ActorScaleConstraint extends ActorAxisConstraint {

    private final TransformComponents _componentsA = new TransformComponents();
    private final TransformComponents _componentsB = new TransformComponents();

    public static ActorScaleConstraint read(ActorArtboard artboard, StreamReader reader,
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
        ActorNode t = (ActorNode) target();
        ActorNode p = parent();
        ActorNode grandParent = p.parent();

        Mat2D transformA = parent().worldTransform();
        Mat2D transformB = new Mat2D();
        Mat2D.decompose(transformA, _componentsA);
        if (t == null) {
            Mat2D.copy(transformB, transformA);
            _componentsB.values()[0] = _componentsA.values()[0];
            _componentsB.values()[1] = _componentsA.values()[1];
            _componentsB.values()[2] = _componentsA.values()[2];
            _componentsB.values()[3] = _componentsA.values()[3];
            _componentsB.values()[4] = _componentsA.values()[4];
            _componentsB.values()[5] = _componentsA.values()[5];
        } else {
            Mat2D.copy(transformB, t.worldTransform());
            if (sourceSpace() == TransformSpace.local) {
                ActorNode sourceGrandParent = t.parent();
                if (sourceGrandParent != null) {
                    Mat2D inverse = new Mat2D();
                    Mat2D.invert(inverse, sourceGrandParent.worldTransform());
                    Mat2D.multiply(transformB, inverse, transformB);
                }
            }
            Mat2D.decompose(transformB, _componentsB);

            if (!copyX()) {
                _componentsB.values()[2] = destSpace() == TransformSpace.local ? 1.0f : _componentsA.values()[2];
            } else {
                _componentsB.values()[2] *= scaleX();
                if (offset()) {
                    _componentsB.values()[2] *= parent().scaleX();
                }
            }

            if (!copyY()) {
                _componentsB.values()[3] = destSpace() == TransformSpace.local ? 0.0f : _componentsA.values()[3];
            } else {
                _componentsB.values()[3] *= scaleY();

                if (offset()) {
                    _componentsB.values()[3] *= parent().scaleY();
                }
            }

            if (destSpace() == TransformSpace.local) {
                // Destination space is in parent() transform coordinates.
                // Recompose the parent() local transform and get it in world,
                // then decompose the world for interpolation.
                if (grandParent != null) {
                    Mat2D.compose(transformB, _componentsB);
                    Mat2D.multiply(transformB, grandParent.worldTransform(), transformB);
                    Mat2D.decompose(transformB, _componentsB);
                }
            }
        }

        boolean clampLocal = minMaxSpace() == TransformSpace.local && grandParent != null;
        if (clampLocal) {
            // Apply min max in local space, so transform to local coordinates first.
            Mat2D.compose(transformB, _componentsB);
            Mat2D inverse = new Mat2D();
            Mat2D.invert(inverse, grandParent.worldTransform());
            Mat2D.multiply(transformB, inverse, transformB);
            Mat2D.decompose(transformB, _componentsB);
        }
        if (enableMaxX() && _componentsB.values()[2] > maxX()) {
            _componentsB.values()[2] = maxX();
        }
        if (enableMinX() && _componentsB.values()[2] < minX()) {
            _componentsB.values()[2] = minX();
        }
        if (enableMaxY() && _componentsB.values()[3] > maxY()) {
            _componentsB.values()[3] = maxY();
        }
        if (enableMinY() && _componentsB.values()[3] < minY()) {
            _componentsB.values()[3] = minY();
        }
        if (clampLocal) {
            // Transform back to world.
            Mat2D.compose(transformB, _componentsB);
            Mat2D.multiply(transformB, grandParent.worldTransform(), transformB);
            Mat2D.decompose(transformB, _componentsB);
        }

        float ti = 1.0f - strength();

        _componentsB.values()[4] = _componentsA.values()[4];
        _componentsB.values()[0] = _componentsA.values()[0];
        _componentsB.values()[1] = _componentsA.values()[1];
        _componentsB.values()[2] = _componentsA.values()[2] * ti + _componentsB.values()[2] * strength();
        _componentsB.values()[3] = _componentsA.values()[3] * ti + _componentsB.values()[3] * strength();
        _componentsB.values()[5] = _componentsA.values()[5];

        Mat2D.compose(parent().worldTransform(), _componentsB);
    }

    @Override
    public void update(int dirt) {
    }

    @Override
    public void completeResolve() {
    }
}
