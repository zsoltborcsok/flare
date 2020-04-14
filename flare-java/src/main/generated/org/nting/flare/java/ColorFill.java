package org.nting.flare.java;

public abstract class ColorFill extends ActorColor with ActorFill {
  public void copyColorFill(ColorFill node, ActorArtboard resetArtboard) {
    copyColor(node, resetArtboard);
    copyFill(node, resetArtboard);
  }

  static ColorFill read(ActorArtboard artboard, StreamReader reader,
      ColorFill component) {
    ActorColor.read(artboard, reader, component);
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
