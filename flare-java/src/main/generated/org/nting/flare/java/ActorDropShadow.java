package org.nting.flare.java;

public class ActorDropShadow extends ActorShadow {
  @override
  int get blendModeId => 0;

  @override
  set blendModeId(int value) {}

  @override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorDropShadow instanceShape = resetArtboard.actor.makeDropShadow();
    instanceShape.copyShadow(this, resetArtboard);
    return instanceShape;
  }
}
