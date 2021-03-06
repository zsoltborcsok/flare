package org.nting.flare.java;

import java.util.List;

public abstract class ActorConstraint extends ActorComponent {

    private boolean _isEnabled;
    private float _strength;

    public boolean isEnabled() {
        return _isEnabled;
    }

    public void isEnabled(boolean value) {
        if (value == _isEnabled) {
            return;
        }
        _isEnabled = value;
        markDirty();
    }

    @Override
    public void onDirty(int dirt) {
        markDirty();
    }

    public float strength() {
        return _strength;
    }

    public void strength(float value) {
        if (value == _strength) {
            return;
        }
        _strength = value;
        markDirty();
    }

    public void markDirty() {
        parent().markTransformDirty();
    }

    public abstract void constrain(ActorNode node);

    @Override
    public void resolveComponentIndices(List<ActorComponent> components) {
        super.resolveComponentIndices(components);
        if (parent() != null) {
            // This works because nodes are exported in hierarchy order,
            // so we are assured constraints get added in order as we resolve indices.
            parent().addConstraint(this);
        }
    }

    public static ActorConstraint read(ActorArtboard artboard, StreamReader reader, ActorConstraint component) {
        ActorComponent.read(artboard, reader, component);
        component._strength = reader.readFloat32("strength");
        component._isEnabled = reader.readBoolean("isEnabled");

        return component;
    }

    public void copyConstraint(ActorConstraint node, ActorArtboard resetArtboard) {
        copyComponent(node, resetArtboard);

        _isEnabled = node._isEnabled;
        _strength = node._strength;
    }
}
