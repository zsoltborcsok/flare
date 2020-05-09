package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class ActorDistanceConstraint extends ActorTargetedConstraint {

    public static class DistanceMode {
        public static final int closer = 0;
        public static final int further = 1;
        public static final int exact = 2;
    }

    private float _distance = 100.0f;
    private int _mode = DistanceMode.closer;

    public static ActorDistanceConstraint read(ActorArtboard artboard, StreamReader reader,
            ActorDistanceConstraint component) {
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

    void copyDistanceConstraint(ActorDistanceConstraint node, ActorArtboard resetArtboard) {
        copyTargetedConstraint(node, resetArtboard);
        _distance = node._distance;
        _mode = node._mode;
    }

    @Override
    public void constrain(ActorNode node) {
        ActorNode t = (ActorNode) target();
        if (t == null) {
            return;
        }

        ActorNode p = parent();
        Vec2D targetTranslation = t.getWorldTranslation(new Vec2D());
        Vec2D ourTranslation = p.getWorldTranslation(new Vec2D());

        Vec2D toTarget = Vec2D.subtract(new Vec2D(), ourTranslation, targetTranslation);
        float currentDistance = Vec2D.length(toTarget);
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

        Vec2D.scale(toTarget, toTarget, 1.0f / currentDistance);
        Vec2D.scale(toTarget, toTarget, _distance);

        Mat2D world = p.worldTransform();
        Vec2D position = Vec2D.lerp(new Vec2D(), ourTranslation, Vec2D.add(new Vec2D(), targetTranslation, toTarget),
                strength());
        world.values()[4] = position.values()[0];
        world.values()[5] = position.values()[1];
    }

    public float distance() {
        return _distance;
    }

    public int mode() {
        return _mode;
    }

    public void distance(float d) {
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
    public void completeResolve() {
    }

    @Override
    public void update(int dirt) {
    }
}
