package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorPath;

import pythagoras.f.Path;

public class FlutterActorPath extends ActorPath implements FlutterPathPointsPath {

    private final Path path = new Path();
    private boolean isValid = false;

    @Override
    public Path path() {
        if (!isValid) {
            isValid = true;
            buildPath(path);
        }
        return path;
    }

    @Override
    public void invalidatePath() {
        isValid = false;
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        FlutterActorPath instanceNode = new FlutterActorPath();
        instanceNode.copyPath(this, resetArtboard);
        return instanceNode;
    }
}
