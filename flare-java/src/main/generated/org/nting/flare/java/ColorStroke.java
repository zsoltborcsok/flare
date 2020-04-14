package org.nting.flare.java;

public abstract class ColorStroke extends ActorColor with ActorStroke {
  public void copyColorStroke(ColorStroke node, ActorArtboard resetArtboard) {
    copyColor(node, resetArtboard);
    copyStroke(node, resetArtboard);
  }

  static ColorStroke read(ActorArtboard artboard, StreamReader reader,
      ColorStroke component) {
    ActorColor.read(artboard, reader, component);
    ActorStroke.read(artboard, reader, component);
    return component;
  }

  @Override
  public void completeResolve() {
    super.completeResolve();

    ActorNode parentNode = parent;
    if (parentNode is ActorShape) {
      parentNode.addStroke(this);
    }
  }
}
