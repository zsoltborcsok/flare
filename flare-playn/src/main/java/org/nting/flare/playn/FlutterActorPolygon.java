package org.nting.flare.playn;

public class FlutterActorPolygon extends ActorPolygon with FlutterPathPointsPath {
  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    FlutterActorPolygon instanceNode = new FlutterActorPolygon();
    instanceNode.copyPolygon(this, resetArtboard);
    return instanceNode;
  }
}
