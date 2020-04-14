package org.nting.flare.java;

public class ActorInnerShadow extends ActorShadow {
  @Override
  int get blendModeId => 0;

  @Override
  set blendModeId(int value) {}

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorInnerShadow instanceShape = resetArtboard.actor.makeInnerShadow();
    instanceShape.copyShadow(this, resetArtboard);
    return instanceShape;
  }
}
