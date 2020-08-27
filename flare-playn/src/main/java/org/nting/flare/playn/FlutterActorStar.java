package org.nting.flare.playn;

public class FlutterActorStar extends ActorStar with FlutterPathPointsPath {
  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    FlutterActorStar instanceNode = new FlutterActorStar();
    instanceNode.copyStar(this, resetArtboard);
    return instanceNode;
  }
}
