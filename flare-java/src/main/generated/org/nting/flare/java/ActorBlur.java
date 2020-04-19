package org.nting.flare.java;

public class ActorBlur extends ActorLayerEffect {
  double blurX;
  double blurY;

  static ActorBlur read(ActorArtboard artboard, StreamReader reader,
      ActorBlur component) {
    component = component != null ? component : new ActorBlur();
    ActorLayerEffect.read(artboard, reader, component);
    component.blurX = reader.readFloat32("blurX");
    component.blurY = reader.readFloat32("blurY");

    return component;
  }

  public void copyBlur(ActorBlur from, ActorArtboard resetArtboard) {
    copyLayerEffect(from, resetArtboard);
    blurX = from.blurX;
    blurY = from.blurY;
  }

  @Override
  public void completeResolve() {
    // intentionally empty, no logic to complete.
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorBlur instanceNode = new ActorBlur();
    instanceNode.copyBlur(this, resetArtboard);
    return instanceNode;
  }

  @Override
  public void onDirty(int dirt) {
    // intentionally empty
  }

  @Override
  public void update(int dirt) {
    // intentionally empty
  }
}
