package org.nting.flare.java;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.List;

import org.nting.flare.java.maths.Vec2D;

import com.google.common.collect.Lists;

public class ActorStar extends ActorProceduralPath {

    private int _numPoints = 5;
    private float _innerRadius = 0.0f;

    @Override
    public void invalidatePath() {
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorStar instance = new ActorStar();
        instance.copyStar(this, resetArtboard);
        return instance;
    }

    public void copyStar(ActorStar node, ActorArtboard resetArtboard) {
        copyPath(node, resetArtboard);
        _numPoints = node._numPoints;
        _innerRadius = node._innerRadius;
    }

    public static ActorStar read(ActorArtboard artboard, StreamReader reader, ActorStar component) {
        component = component != null ? component : new ActorStar();

        ActorNode.read(artboard, reader, component);

        component.width(reader.readFloat32("width"));
        component.height(reader.readFloat32("height"));
        component._numPoints = reader.readInt32("points");
        component._innerRadius = reader.readFloat32("innerRadius");
        return component;
    }

    @Override
    public List<PathPoint> points() {
        List<PathPoint> _starPoints = Lists.newArrayList(new StraightPathPoint(new Vec2D(0.0f, -radiusY())));

        float angle = (float) (-Math.PI / 2.0);
        float inc = (float) ((Math.PI * 2.0) / sides());
        Vec2D sx = new Vec2D(radiusX(), radiusX() * _innerRadius);
        Vec2D sy = new Vec2D(radiusY(), radiusY() * _innerRadius);

        for (int i = 0; i < sides(); i++) {
            _starPoints.add(new StraightPathPoint(
                    new Vec2D((float) cos(angle) * sx.values()[i % 2], (float) sin(angle) * sy.values()[i % 2])));
            angle += inc;
        }
        return _starPoints;
    }

    public void innerRadius(float val) {
        if (val != _innerRadius) {
            _innerRadius = val;
            invalidateDrawable();
        }
    }

    public float innerRadius() {
        return _innerRadius;
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

    public int numPoints() {
        return _numPoints;
    }

    public int sides() {
        return _numPoints * 2;
    }
}
