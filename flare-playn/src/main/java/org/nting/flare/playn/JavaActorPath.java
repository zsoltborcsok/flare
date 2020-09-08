package org.nting.flare.playn;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorPath;

import pythagoras.f.Path;

public class JavaActorPath extends ActorPath implements JavaPathPointsPath {

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
        JavaActorPath instanceNode = new JavaActorPath();
        instanceNode.copyPath(this, resetArtboard);
        return instanceNode;
    }
}
