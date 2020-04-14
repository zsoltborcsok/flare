package org.nting.flare.java;

public abstract class GradientFill extends GradientColor with ActorFill {
  public void copyGradientFill(GradientFill node, ActorArtboard resetArtboard) {
    copyGradient(node, resetArtboard);
    copyFill(node, resetArtboard);
  }

  static GradientFill read(ActorArtboard artboard, StreamReader reader,
      GradientFill component) {
    GradientColor.read(artboard, reader, component);
    component._fillRule = fillRuleLookup[reader.readUint8("fillRule")];
    return component;
  }

  @Override
  public void completeResolve() {
    super.completeResolve();

    ActorNode parentNode = parent;
    if (parentNode is ActorShape) {
      parentNode.addFill(this);
    }
  }
}
