package org.nting.flare.java;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

import com.google.common.collect.Lists;

public class ActorShape extends ActorDrawable {

    private final List<ActorBasePath> _paths = new ArrayList<>();
    private final List<ActorStroke> _strokes = new ArrayList<>();
    private final List<ActorFill> _fills = new ArrayList<>();
    private boolean _transformAffectsStroke = false;

    public boolean transformAffectsStroke() {
        return _transformAffectsStroke;
    }

    public ActorFill fill() {
        return !_fills.isEmpty() ? _fills.get(0) : null;
    }

    public ActorStroke stroke() {
        return !_strokes.isEmpty() ? _strokes.get(0) : null;
    }

    public List<ActorFill> fills() {
        return _fills;
    }

    public List<ActorStroke> strokes() {
        return _strokes;
    }

    public List<ActorBasePath> paths() {
        return _paths;
    }

    @Override
    public void update(int dirt) {
        super.update(dirt);
        invalidateShape();
    }

    public static ActorShape read(ActorArtboard artboard, StreamReader reader, ActorShape component) {
        ActorDrawable.read(artboard, reader, component);
        if (artboard.actor().version() >= 22) {
            component._transformAffectsStroke = reader.readBoolean("transformAffectsStroke");
        }

        return component;
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorShape instanceShape = resetArtboard.actor().makeShapeNode(this);
        instanceShape.copyShape(this, resetArtboard);
        return instanceShape;
    }

    public void copyShape(ActorShape node, ActorArtboard resetArtboard) {
        copyDrawable(node, resetArtboard);
        _transformAffectsStroke = node._transformAffectsStroke;
    }

    @Override
    public AABB computeAABB() {
        AABB aabb = null;
        for (final List<ClipShape> clips : clipShapes()) {
            for (final ClipShape clipShape : clips) {
                AABB bounds = clipShape.shape.computeAABB();
                if (bounds == null) {
                    continue;
                }
                if (aabb == null) {
                    aabb = bounds;
                } else {
                    if (bounds.values()[0] < aabb.values()[0]) {
                        aabb.values()[0] = bounds.values()[0];
                    }
                    if (bounds.values()[1] < aabb.values()[1]) {
                        aabb.values()[1] = bounds.values()[1];
                    }
                    if (bounds.values()[2] > aabb.values()[2]) {
                        aabb.values()[2] = bounds.values()[2];
                    }
                    if (bounds.values()[3] > aabb.values()[3]) {
                        aabb.values()[3] = bounds.values()[3];
                    }
                }
            }
        }
        if (aabb != null) {
            return aabb;
        }

        for (final ActorComponent component : children()) {
            if (!(component instanceof ActorBasePath)) {
                continue;
            }
            ActorBasePath path = (ActorBasePath) component;
            // This is the axis aligned bounding box in the space of the
            // parent (this case our shape).
            AABB pathAABB = path.getPathAABB();

            if (aabb == null) {
                aabb = pathAABB;
            } else {
                // Combine.
                aabb.values()[0] = min(aabb.values()[0], pathAABB.values()[0]);
                aabb.values()[1] = min(aabb.values()[1], pathAABB.values()[1]);

                aabb.values()[2] = max(aabb.values()[2], pathAABB.values()[2]);
                aabb.values()[3] = max(aabb.values()[3], pathAABB.values()[3]);
            }
        }

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        if (aabb == null) {
            return new AABB(minX, minY, maxX, maxY);
        }
        Mat2D world = worldTransform();

        if (_strokes != null) {
            float maxStroke = 0.0f;
            for (final ActorStroke stroke : _strokes) {
                if (stroke.width() > maxStroke) {
                    maxStroke = stroke.width();
                }
            }
            float padStroke = maxStroke / 2.0f;
            aabb.values()[0] -= padStroke;
            aabb.values()[2] += padStroke;
            aabb.values()[1] -= padStroke;
            aabb.values()[3] += padStroke;
        }

        List<Vec2D> points = Lists.newArrayList(new Vec2D(aabb.values()[0], aabb.values()[1]),
                new Vec2D(aabb.values()[2], aabb.values()[1]), new Vec2D(aabb.values()[2], aabb.values()[3]),
                new Vec2D(aabb.values()[0], aabb.values()[3]));
        for (Vec2D pt : points) {
            Vec2D wp = Vec2D.transformMat2D(pt, pt, world);
            if (wp.values()[0] < minX) {
                minX = wp.values()[0];
            }
            if (wp.values()[1] < minY) {
                minY = wp.values()[1];
            }

            if (wp.values()[0] > maxX) {
                maxX = wp.values()[0];
            }
            if (wp.values()[1] > maxY) {
                maxY = wp.values()[1];
            }
        }
        return new AABB(minX, minY, maxX, maxY);
    }

    public void addStroke(ActorStroke stroke) {
        _strokes.add(stroke);
    }

    public void addFill(ActorFill fill) {
        _fills.add(fill);
    }

    @Override
    public int blendModeId() {
        return 0;
    }

    @Override
    public void blendModeId(int value) {
    }

    public boolean addPath(ActorBasePath path) {
        if (_paths.contains(path)) {
            return false;
        }
        _paths.add(path);
        return true;
    }

    public boolean removePath(ActorBasePath path) {
        return _paths.remove(path);
    }
}
