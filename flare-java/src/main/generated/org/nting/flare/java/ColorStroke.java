package org.nting.flare.java;

import static com.google.common.util.concurrent.Runnables.doNothing;

public abstract class ColorStroke extends ActorColor {

    private final ActorStroke actorStroke = new ActorStroke(this::markPaintDirty, doNothing(),
            this::initializeGraphics);

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

    public abstract void initializeGraphics();
}
