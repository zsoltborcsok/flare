package org.nting.flare.java;

import java.util.List;

public abstract class ActorTargetedConstraint extends ActorConstraint {

    private int _targetIdx;
    private ActorComponent _target;

    public ActorComponent target() {
        return _target;
    }

    @Override
    public void resolveComponentIndices(List<ActorComponent> components) {
        super.resolveComponentIndices(components);
        if (_targetIdx != 0) {
            _target = components.get(_targetIdx);
            if (_target != null) {
                artboard.addDependency(parent(), _target);
            }
        }
    }

    public static ActorTargetedConstraint read(ActorArtboard artboard, StreamReader reader,
            ActorTargetedConstraint component) {
        ActorConstraint.read(artboard, reader, component);
        component._targetIdx = reader.readId("target");

        return component;
    }

    void copyTargetedConstraint(ActorTargetedConstraint node, ActorArtboard resetArtboard) {
        copyConstraint(node, resetArtboard);

        _targetIdx = node._targetIdx;
    }
}
