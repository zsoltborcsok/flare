package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorEllipse;

public class FlutterActorEllipse extends ActorEllipse implements FlutterPathPointsPath {
    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterActorEllipse instanceNode = new FlutterActorEllipse();
        instanceNode.copyPath(this, resetArtboard);
        return instanceNode;
    }
}
