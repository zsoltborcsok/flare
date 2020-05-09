package org.nting.flare.java;

public class ActorFill {

    private final Runnable initializeGraphics;

    private FillRule _fillRule = FillRule.evenOdd;

    public ActorFill(Runnable initializeGraphics) {
        this.initializeGraphics = initializeGraphics;
    }

    public FillRule fillRule() {
        return _fillRule;
    }

    public void fillRule(FillRule fillRule) {
        _fillRule = fillRule;
    }

    public static void read(ActorArtboard artboard, StreamReader reader, ActorFill component) {
        component._fillRule = FillRule.values()[reader.readUint8("fillRule")];
    }

    public void copyFill(ActorFill node, ActorArtboard resetArtboard) {
        _fillRule = node._fillRule;
    }

    public void initializeGraphics() {
        initializeGraphics.run();
    }
}
