package org.nting.flare.java;

public abstract class RadialGradientFill extends RadialGradientColor {

    public final ActorFill actorFill = new ActorFill(this);

    public void copyRadialFill(RadialGradientFill node, ActorArtboard resetArtboard) {
        copyRadialGradient(node, resetArtboard);
        actorFill.copyFill(node.actorFill, resetArtboard);
    }

    public static RadialGradientFill read(ActorArtboard artboard, StreamReader reader, RadialGradientFill component) {
        RadialGradientColor.read(artboard, reader, component);
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
