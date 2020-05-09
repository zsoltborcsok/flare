package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class ActorTranslationConstraint extends ActorAxisConstraint {

    public static ActorTranslationConstraint read(ActorArtboard artboard, StreamReader reader,
            ActorTranslationConstraint component) {
        component = component != null ? component : new ActorTranslationConstraint();
        ActorAxisConstraint.read(artboard, reader, component);

        return component;
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorTranslationConstraint node = new ActorTranslationConstraint();
        node.copyAxisConstraint(this, resetArtboard);
        return node;
    }

    @Override
    public void constrain(ActorNode node) {
        ActorNode t = (ActorNode) target();
        ActorNode p = parent();
        ActorNode grandParent = p.parent();

        Mat2D transformA = parent().worldTransform();
        Vec2D translationA = new Vec2D(transformA.values()[4], transformA.values()[5]);
        Vec2D translationB = new Vec2D();

        if (t == null) {
            Vec2D.copy(translationB, translationA);
        } else {
            Mat2D transformB = new Mat2D(t.worldTransform());
            if (sourceSpace() == TransformSpace.local) {
                ActorNode sourceGrandParent = t.parent();
                if (sourceGrandParent != null) {
                    Mat2D inverse = new Mat2D();
                    Mat2D.invert(inverse, sourceGrandParent.worldTransform());
                    Mat2D.multiply(transformB, inverse, transformB);
                }
            }
            translationB.values()[0] = transformB.values()[4];
            translationB.values()[1] = transformB.values()[5];

            if (!copyX()) {
                translationB.values()[0] = destSpace() == TransformSpace.local ? 0.0f : translationA.values()[0];
            } else {
                translationB.values()[0] *= scaleX();
                if (offset()) {
                    translationB.values()[0] += parent().translation().values()[0];
                }
            }

            if (!copyY()) {
                translationB.values()[1] = destSpace() == TransformSpace.local ? 0.0f : translationA.values()[1];
            } else {
                translationB.values()[1] *= scaleY();
                if (offset()) {
                    translationB.values()[1] += parent().translation().values()[1];
                }
            }

            if (destSpace() == TransformSpace.local) {
                if (grandParent != null) {
                    Vec2D.transformMat2D(translationB, translationB, grandParent.worldTransform());
                }
            }
        }

        boolean clampLocal = minMaxSpace() == TransformSpace.local && grandParent != null;
        if (clampLocal) {
            // Apply min max in local space, so transform to local coordinates first.
            Mat2D temp = new Mat2D();
            Mat2D.invert(temp, grandParent.worldTransform());
            // Get our target world coordinates in parent() local.
            Vec2D.transformMat2D(translationB, translationB, temp);
        }
        if (enableMaxX() && translationB.values()[0] > maxX()) {
            translationB.values()[0] = maxX();
        }
        if (enableMinX() && translationB.values()[0] < minX()) {
            translationB.values()[0] = minX();
        }
        if (enableMaxY() && translationB.values()[1] > maxY()) {
            translationB.values()[1] = maxY();
        }
        if (enableMinY() && translationB.values()[1] < minY()) {
            translationB.values()[1] = minY();
        }
        if (clampLocal) {
            // Transform back to world.
            Vec2D.transformMat2D(translationB, translationB, grandParent.worldTransform());
        }

        float ti = 1.0f - strength();

        // Just interpolate world translation
        transformA.values()[4] = translationA.values()[0] * ti + translationB.values()[0] * strength();
        transformA.values()[5] = translationA.values()[1] * ti + translationB.values()[1] * strength();
    }

    @Override
    public void update(int dirt) {
    }

    @Override
    public void completeResolve() {
    }
}
