package org.nting.flare.java;

public abstract class RadialGradientStroke extends RadialGradientColor
    with ActorStroke {
  void copyRadialStroke(RadialGradientStroke node,
      ActorArtboard resetArtboard) {
    copyRadialGradient(node, resetArtboard);
    copyStroke(node, resetArtboard);
  }

  static RadialGradientStroke read(ActorArtboard artboard, StreamReader reader,
      RadialGradientStroke component) {
    RadialGradientColor.read(artboard, reader, component);
    ActorStroke.read(artboard, reader, component);
    return component;
  }

  @Override
  public void completeResolve() {
    super.completeResolve();

    ActorNode parentNode = parent;
    if (parentNode instanceof ActorShape) {
      parentNode.addStroke(this);
    }
  }
}
