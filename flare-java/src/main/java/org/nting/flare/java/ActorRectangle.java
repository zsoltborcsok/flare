package org.nting.flare.java;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.maths.Vec2D;

public class ActorRectangle extends ActorProceduralPath {

    private float _radius = 0.0f;

    @Override
    public void invalidatePath() {
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorRectangle instance = new ActorRectangle();
        instance.copyRectangle(this, resetArtboard);
        return instance;
    }

    public void copyRectangle(ActorRectangle node, ActorArtboard resetArtboard) {
        copyPath(node, resetArtboard);
        _radius = node._radius;
    }

    public static ActorRectangle read(ActorArtboard artboard, StreamReader reader, ActorRectangle component) {
        component = component != null ? component : new ActorRectangle();

        ActorNode.read(artboard, reader, component);

        component.width(reader.readFloat32("width"));
        component.height(reader.readFloat32("height"));
        component._radius = reader.readFloat32("cornerRadius");
        return component;
    }

    @Override
    public List<PathPoint> points() {
        float halfWidth = width() / 2;
        float halfHeight = height() / 2;
        float renderRadius = min(_radius, min(halfWidth, halfHeight));
        List<PathPoint> _rectanglePathPoints = new ArrayList<PathPoint>();
        _rectanglePathPoints.add(new StraightPathPoint(new Vec2D(-halfWidth, -halfHeight), renderRadius));
        _rectanglePathPoints.add(new StraightPathPoint(new Vec2D(halfWidth, -halfHeight), renderRadius));
        _rectanglePathPoints.add(new StraightPathPoint(new Vec2D(halfWidth, halfHeight), renderRadius));
        _rectanglePathPoints.add(new StraightPathPoint(new Vec2D(-halfWidth, halfHeight), renderRadius));

        return _rectanglePathPoints;
    }

    public void radius(float rd) {
        if (rd != _radius) {
            _radius = rd;
            invalidateDrawable();
        }
    }

    public boolean isClosed() {
        return true;
    }

    public boolean doesDraw() {
        return !renderCollapsed();
    }

    public float radius() {
        return _radius;
    }
}
