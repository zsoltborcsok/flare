package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.TransformComponents;

public class ActorTransformConstraint extends ActorTargetedConstraint {

    private static final float pi2 = (float) (Math.PI * 2);

    private int _sourceSpace = TransformSpace.world;
    private int _destSpace = TransformSpace.world;
    private final TransformComponents _componentsA = new TransformComponents();
    private final TransformComponents _componentsB = new TransformComponents();

    public static ActorTransformConstraint read(ActorArtboard artboard, StreamReader reader,
            ActorTransformConstraint component) {
        component = component != null ? component : new ActorTransformConstraint();
        ActorTargetedConstraint.read(artboard, reader, component);

        component._sourceSpace = reader.readUint8("sourceSpaceId");
        component._destSpace = reader.readUint8("destSpaceId");

        return component;
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorTransformConstraint node = new ActorTransformConstraint();
        node.copyTransformConstraint(this, resetArtboard);
        return node;
    }

    void copyTransformConstraint(ActorTransformConstraint node, ActorArtboard resetArtboard) {
        copyTargetedConstraint(node, resetArtboard);
        _sourceSpace = node._sourceSpace;
        _destSpace = node._destSpace;
    }

    @Override
    public void constrain(ActorNode node) {
        ActorNode t = (ActorNode) target();
        if (t == null) {
            return;
        }

        ActorNode parent = this.parent();

        Mat2D transformA = parent.worldTransform();
        Mat2D transformB = new Mat2D(t.worldTransform());
        if (_sourceSpace == TransformSpace.local) {
            ActorNode grandParent = target().parent();
            if (grandParent != null) {
                Mat2D inverse = new Mat2D();
                Mat2D.invert(inverse, grandParent.worldTransform());
                Mat2D.multiply(transformB, inverse, transformB);
            }
        }
        if (_destSpace == TransformSpace.local) {
            ActorNode grandParent = parent.parent();
            if (grandParent != null) {
                Mat2D.multiply(transformB, grandParent.worldTransform(), transformB);
            }
        }
        Mat2D.decompose(transformA, _componentsA);
        Mat2D.decompose(transformB, _componentsB);

        float angleA = _componentsA.values()[4] % pi2;
        float angleB = _componentsB.values()[4] % pi2;
        float diff = angleB - angleA;
        if (diff > Math.PI) {
            diff -= pi2;
        } else if (diff < -Math.PI) {
            diff += pi2;
        }

        float ti = 1.0f - strength();

        _componentsB.values()[4] = angleA + diff * strength();
        _componentsB.values()[0] = _componentsA.values()[0] * ti + _componentsB.values()[0] * strength();
        _componentsB.values()[1] = _componentsA.values()[1] * ti + _componentsB.values()[1] * strength();
        _componentsB.values()[2] = _componentsA.values()[2] * ti + _componentsB.values()[2] * strength();
        _componentsB.values()[3] = _componentsA.values()[3] * ti + _componentsB.values()[3] * strength();
        _componentsB.values()[5] = _componentsA.values()[5] * ti + _componentsB.values()[5] * strength();

        Mat2D.compose(parent.worldTransform(), _componentsB);
    }

    @Override
    public void update(int dirt) {
    }

    @Override
    public void completeResolve() {
    }
}
