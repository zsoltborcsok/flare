package org.nting.flare.java;

public abstract class RadialGradientFill extends RadialGradientColor with ActorFill {
  public void copyRadialFill(RadialGradientFill node, ActorArtboard resetArtboard) {
    copyRadialGradient(node, resetArtboard);
    copyFill(node, resetArtboard);
  }

  static RadialGradientFill read(ActorArtboard artboard, StreamReader reader,
      RadialGradientFill component) {
    RadialGradientColor.read(artboard, reader, component);
    ActorFill.read(artboard, reader, component);

    return component;
  }

  @Override
  public void completeResolve() {
    super.completeResolve();

    ActorNode parentNode = parent;
    if (parentNode instanceof ActorShape) {
      parentNode.addFill(this);
    }
  }
}
