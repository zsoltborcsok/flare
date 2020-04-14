package org.nting.flare.java;

public abstract class ActorLayerEffect extends ActorComponent {
  boolean _isActive;

  boolean get isActive => _isActive;

  static ActorLayerEffect read(ActorArtboard artboard, StreamReader reader,
      ActorLayerEffect component) {
    ActorComponent.read(artboard, reader, component);
    component._isActive = reader.readBoolean("isActive");

    return component;
  }

  public void copyLayerEffect(ActorLayerEffect from, ActorArtboard resetArtboard) {
    copyComponent(from, resetArtboard);
    _isActive = from._isActive;
  }
}
