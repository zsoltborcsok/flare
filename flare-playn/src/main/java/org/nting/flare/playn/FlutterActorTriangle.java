package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorTriangle;

public class FlutterActorTriangle extends ActorTriangle implements FlutterPathPointsPath {

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterActorTriangle instanceNode = new FlutterActorTriangle();
        instanceNode.copyPath(this, resetArtboard);
        return instanceNode;
    }
}
