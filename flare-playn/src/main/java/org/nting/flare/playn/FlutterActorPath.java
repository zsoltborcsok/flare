package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorPath;

public class FlutterActorPath extends ActorPath implements FlutterPathPointsPath {

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterActorPath instanceNode = new FlutterActorPath();
        instanceNode.copyPath(this, resetArtboard);
        return instanceNode;
    }
}
