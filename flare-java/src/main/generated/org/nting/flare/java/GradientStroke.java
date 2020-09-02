package org.nting.flare.java;

public abstract class GradientStroke extends GradientColor {

    private final ActorStroke actorStroke = new ActorStroke(this::markPaintDirty, this::markPathEffectsDirty);

    public void copyGradientStroke(GradientStroke node, ActorArtboard resetArtboard) {
        copyGradient(node, resetArtboard);
        actorStroke.copyStroke(node.actorStroke, resetArtboard);
    }

    public static GradientStroke read(ActorArtboard artboard, StreamReader reader, GradientStroke component) {
        GradientColor.read(artboard, reader, component);
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
