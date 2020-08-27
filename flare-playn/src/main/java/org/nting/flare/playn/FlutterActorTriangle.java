package org.nting.flare.playn;

public class FlutterActorTriangle extends ActorTriangle with FlutterPathPointsPath {
  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    FlutterActorTriangle instanceNode = new FlutterActorTriangle();
    instanceNode.copyPath(this, resetArtboard);
    return instanceNode;
  }
}
