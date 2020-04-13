package org.nting.flare.java;

public class ActorInnerShadow extends ActorShadow {
  @override
  int get blendModeId => 0;

  @override
  set blendModeId(int value) {}

  @override
  ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorInnerShadow instanceShape = resetArtboard.actor.makeInnerShadow();
    instanceShape.copyShadow(this, resetArtboard);
    return instanceShape;
  }
}
