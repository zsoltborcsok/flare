package org.nting.flare.java;

public abstract class ColorStroke extends ActorColor {

    public final ActorStroke actorStroke = new ActorStroke(this, this::markPaintDirty, this::markPathEffectsDirty);

    public void copyColorStroke(ColorStroke node, ActorArtboard resetArtboard) {
        copyColor(node, resetArtboard);
        actorStroke.copyStroke(node.actorStroke, resetArtboard);
    }

    public static ColorStroke read(ActorArtboard artboard, StreamReader reader, ColorStroke component) {
        ActorColor.read(artboard, reader, component);
        ActorStroke.read(artboard, reader, component.actorStroke);
        return component;
    }

    @Override
    public void completeResolve() {
        super.completeResolve();

        ActorNode parentNode = parent();
        if (parentNode instanceof ActorShape) {
            ((ActorShape) parentNode).addStroke(actorStroke);
        }
    }

    public abstract void markPathEffectsDirty();
}
