package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorRectangle;

public class FlutterActorRectangle extends ActorRectangle implements FlutterPathPointsPath {

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterActorRectangle instanceNode = new FlutterActorRectangle();
        instanceNode.copyRectangle(this, resetArtboard);
        return instanceNode;
    }
}
