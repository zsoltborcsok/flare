package org.nting.flare.java;

public abstract class ActorPaint extends ActorComponent {

    private float _opacity = 1.0f;

    public float opacity() {
        return _opacity;
    }

    public void opacity(float value) {
        if (value == _opacity) {
            return;
        }
        _opacity = value;
        markPaintDirty();
    }

    public void copyPaint(ActorPaint component, ActorArtboard resetArtboard) {
        copyComponent(component, resetArtboard);
        opacity(component.opacity());
    }

    public static ActorPaint read(ActorArtboard artboard, StreamReader reader, ActorPaint component) {
        ActorComponent.read(artboard, reader, component);
        component.opacity(reader.readFloat32("opacity"));

        return component;
    }

    @Override
    public void completeResolve() {
        artboard.addDependency(this, parent());
    }

    public ActorShape shape() {
        return (ActorShape) parent();
    }

    public void markPaintDirty() {
        artboard.addDirt(this, DirtyFlags.paintDirty, false);
    }
}
