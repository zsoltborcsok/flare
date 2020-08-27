package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorPolygon;

public class FlutterActorPolygon extends ActorPolygon implements FlutterPathPointsPath {

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterActorPolygon instanceNode = new FlutterActorPolygon();
        instanceNode.copyPolygon(this, resetArtboard);
        return instanceNode;
    }
}
