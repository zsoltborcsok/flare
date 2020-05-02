package org.nting.flare.java;

public class ActorInnerShadow extends ActorShadow {
  @Override
  public int blendModeId() { return 0; }

  @Override
  public void blendModeId(int value) {}

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorInnerShadow instanceShape = resetArtboard.actor.makeInnerShadow();
    instanceShape.copyShadow(this, resetArtboard);
    return instanceShape;
  }
}
