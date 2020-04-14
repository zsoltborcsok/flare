package org.nting.flare.java;

public abstract class GradientStroke extends GradientColor with ActorStroke {
  public void copyGradientStroke(GradientStroke node, ActorArtboard resetArtboard) {
    copyGradient(node, resetArtboard);
    copyStroke(node, resetArtboard);
  }

  static GradientStroke read(ActorArtboard artboard, StreamReader reader,
      GradientStroke component) {
    GradientColor.read(artboard, reader, component);
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
