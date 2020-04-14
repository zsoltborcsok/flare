package org.nting.flare.java;

public class ActorDropShadow extends ActorShadow {
  @Override
  int get blendModeId => 0;

  @Override
  set blendModeId(int value) {}

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorDropShadow instanceShape = resetArtboard.actor.makeDropShadow();
    instanceShape.copyShadow(this, resetArtboard);
    return instanceShape;
  }
}
