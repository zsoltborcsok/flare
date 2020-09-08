package org.nting.flare.java;

public abstract class ColorFill extends ActorColor {

    public final ActorFill actorFill = new ActorFill(this);

    public void copyColorFill(ColorFill node, ActorArtboard resetArtboard) {
        copyColor(node, resetArtboard);
        actorFill.copyFill(node.actorFill, resetArtboard);
    }

    public static ColorFill read(ActorArtboard artboard, StreamReader reader, ColorFill component) {
        ActorColor.read(artboard, reader, component);
        ActorFill.read(artboard, reader, component.actorFill);
        return component;
    }

    @Override
    public void completeResolve() {
        super.completeResolve();

        ActorNode parentNode = parent();
        if (parentNode instanceof ActorShape) {
            ((ActorShape) parentNode).addFill(actorFill);
        }
    }
}
