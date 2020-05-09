package org.nting.flare.java;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.maths.Vec2D;

public class ActorTriangle extends ActorProceduralPath {

    @Override
    public void invalidatePath() {
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorTriangle instance = new ActorTriangle();
        instance.copyPath(this, resetArtboard);
        return instance;
    }

    public static ActorTriangle read(ActorArtboard artboard, StreamReader reader, ActorTriangle component) {
        component = component != null ? component : new ActorTriangle();

        ActorNode.read(artboard, reader, component);

        component.width(reader.readFloat32("width"));
        component.height(reader.readFloat32("height"));
        return component;
    }

    @Override
    public List<PathPoint> points() {
        List<PathPoint> _trianglePoints = new ArrayList<PathPoint>();
        _trianglePoints.add(new StraightPathPoint(new Vec2D(0.0f, -radiusY())));
        _trianglePoints.add(new StraightPathPoint(new Vec2D(radiusX(), radiusY())));
        _trianglePoints.add(new StraightPathPoint(new Vec2D(-radiusX(), radiusY())));

        return _trianglePoints;
    }

    public boolean isClosed() {
        return true;
    }

    public boolean doesDraw() {
        return !renderCollapsed();
    }

    public float radiusX() {
        return width() / 2;
    }

    public float radiusY() {
        return height() / 2;
    }
}
