package org.nting.flare.java;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.maths.Vec2D;

public class ActorEllipse extends ActorProceduralPath {

    public static final float circleConstant = 0.55f;

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorEllipse instance = new ActorEllipse();
        instance.copyPath(this, resetArtboard);
        return instance;
    }

    @Override
    public void invalidatePath() {
    }

    public static ActorEllipse read(ActorArtboard artboard, StreamReader reader, ActorEllipse component) {
        component = component != null ? component : new ActorEllipse();

        ActorNode.read(artboard, reader, component);

        component.width(reader.readFloat32("width"));
        component.height(reader.readFloat32("height"));
        return component;
    }

    @Override
    public List<PathPoint> points() {
        List<PathPoint> _ellipsePathPoints = new ArrayList<PathPoint>();
        _ellipsePathPoints.add(new CubicPathPoint(new Vec2D(0.0f, -radiusY()),
                new Vec2D(-radiusX() * circleConstant, -radiusY()), new Vec2D(radiusX() * circleConstant, -radiusY())));
        _ellipsePathPoints.add(new CubicPathPoint(new Vec2D(radiusX(), 0.0f),
                new Vec2D(radiusX(), circleConstant * -radiusY()), new Vec2D(radiusX(), circleConstant * radiusY())));
        _ellipsePathPoints.add(new CubicPathPoint(new Vec2D(0.0f, radiusY()),
                new Vec2D(radiusX() * circleConstant, radiusY()), new Vec2D(-radiusX() * circleConstant, radiusY())));
        _ellipsePathPoints.add(new CubicPathPoint(new Vec2D(-radiusX(), 0.0f),
                new Vec2D(-radiusX(), radiusY() * circleConstant), new Vec2D(-radiusX(), -radiusY() * circleConstant)));

        return _ellipsePathPoints;
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
