package org.nting.flare.java;

import static com.google.common.util.concurrent.Runnables.doNothing;

public abstract class RadialGradientStroke extends RadialGradientColor {

    private final ActorStroke actorStroke = new ActorStroke(this::markPaintDirty, doNothing(),
            this::initializeGraphics);

    void copyRadialStroke(RadialGradientStroke node, ActorArtboard resetArtboard) {
        copyRadialGradient(node, resetArtboard);
        actorStroke.copyStroke(node.actorStroke, resetArtboard);
    }

    public static RadialGradientStroke read(ActorArtboard artboard, StreamReader reader,
            RadialGradientStroke component) {
        RadialGradientColor.read(artboard, reader, component);
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

    public abstract void initializeGraphics();
}
