package org.nting.flare.playn;

public class FlutterActorRectangle extends ActorRectangle with FlutterPathPointsPath {
  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    FlutterActorRectangle instanceNode = new FlutterActorRectangle();
    instanceNode.copyRectangle(this, resetArtboard);
    return instanceNode;
  }
}
