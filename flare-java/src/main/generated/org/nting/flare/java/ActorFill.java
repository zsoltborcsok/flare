package org.nting.flare.java;

public abstract class ActorFill {
  private FillRule _fillRule = FillRule.evenOdd;

  public FillRule fillRule() { return _fillRule; }

  static void read(ActorArtboard artboard, StreamReader reader,
      ActorFill component) {
    component._fillRule = FillRule.values()[reader.readUint8("fillRule")];
  }

  public void copyFill(ActorFill node, ActorArtboard resetArtboard) {
    _fillRule = node._fillRule;
  }

  public abstract void initializeGraphics();
}
