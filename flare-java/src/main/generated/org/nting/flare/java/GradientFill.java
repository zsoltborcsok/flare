package org.nting.flare.java;

public abstract class GradientFill extends GradientColor {

    private final ActorFill actorFill = new ActorFill(this);

    public void copyGradientFill(GradientFill node, ActorArtboard resetArtboard) {
        copyGradient(node, resetArtboard);
        actorFill.copyFill(node.actorFill, resetArtboard);
    }

    public static GradientFill read(ActorArtboard artboard, StreamReader reader, GradientFill component) {
        GradientColor.read(artboard, reader, component);
        component.actorFill.fillRule(FillRule.values()[reader.readUint8("fillRule")]);
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
