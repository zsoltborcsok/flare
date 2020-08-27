package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorStar;

public class FlutterActorStar extends ActorStar implements FlutterPathPointsPath {

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterActorStar instanceNode = new FlutterActorStar();
        instanceNode.copyStar(this, resetArtboard);
        return instanceNode;
    }
}
