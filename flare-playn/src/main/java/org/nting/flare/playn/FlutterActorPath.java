package org.nting.flare.playn;

public class FlutterActorPath extends ActorPath with FlutterPathPointsPath {
  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    FlutterActorPath instanceNode = new FlutterActorPath();
    instanceNode.copyPath(this, resetArtboard);
    return instanceNode;
  }
}
