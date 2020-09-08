package org.nting.flare.playn;

import java.util.List;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorComponent;
import org.nting.flare.java.ActorPolygon;
import org.nting.flare.java.PathPoint;

import pythagoras.f.Path;

public class JavaActorPolygon extends ActorPolygon implements JavaPathPointsPath {

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
    public List<PathPoint> deformedPoints() {
        return super.deformedPoints();
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        JavaActorPolygon instanceNode = new JavaActorPolygon();
        instanceNode.copyPolygon(this, resetArtboard);
        return instanceNode;
    }
}
