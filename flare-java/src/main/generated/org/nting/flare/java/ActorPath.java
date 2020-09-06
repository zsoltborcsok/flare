package org.nting.flare.java;

import static org.nting.flare.java.PointType.straight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nting.flare.java.maths.Mat2D;

public class ActorPath extends ActorNode implements ActorBasePath, ActorSkinnable {

    private ActorShape _shape;
    private boolean _isRootPath = false;

    private ActorSkin _skin;
    private List<SkinnedBone> _connectedBones;

    private boolean _isHidden;
    private boolean _isClosed;
    private List<PathPoint> _points;
    public float[] vertexDeform;

    public ActorShape shape() {
        return _shape;
    }

    public void shape(ActorShape shape) {
        _shape = shape;
    }

    public boolean isRootPath() {
        return _isRootPath;
    }

    @Override
    public void setRootPath(boolean isRootPath) {
        _isRootPath = isRootPath;
    }

    @Override
    public boolean isPathInWorldSpace() {
        return isConnectedToBones();
    }

    @Override
    public ActorSkin skin() {
        return _skin;
    }

    @Override
    public void skin(ActorSkin skin) {
        _skin = skin;
    }

    @Override
    public List<SkinnedBone> connectedBones() {
        return _connectedBones;
    }

    @Override
    public void connectedBones(List<SkinnedBone> connectedBones) {
        _connectedBones = connectedBones;
    }

    @Override
    public void invalidatePath() {
        // Up to the implementation.
    }

    @Override
    public Mat2D pathTransform() {
        return isConnectedToBones() ? new Mat2D() : worldTransform();
    }

    public static final int vertexDeformDirty = 1 << 1;

    @Override
    public List<PathPoint> points() {
        return _points;
    }

    @Override
    public List<PathPoint> deformedPoints() {
        if (!isConnectedToBones() || skin() == null) {
            return _points;
        }

        float[] boneMatrices = skin().boneMatrices();
        List<PathPoint> deformed = new ArrayList<PathPoint>();
        for (final PathPoint point : _points) {
            deformed.add(point.skin(worldTransform(), boneMatrices));
        }
        return deformed;
    }

    @Override
    public void invalidateDrawable() {
        ActorBasePath.super.invalidateDrawable();
    }

    public boolean isClosed() {
        return _isClosed;
    }

    @Override
    public void onDirty(int dirt) {
        super.onDirty(dirt);
        // We transformed, make sure parent is invalidated.
        if (shape() != null) {
            shape().invalidateShape();
        }
    }

    public void makeVertexDeform() {
        if (vertexDeform != null) {
            return;
        }
        int length = 0;
        for (PathPoint point : points()) {
            length += 2 + (point.pointType() == straight ? 1 : 4);
        }
        float[] vertices = new float[length];
        int readIdx = 0;
        for (final PathPoint point : points()) {
            vertices[readIdx++] = point.translation().values()[0];
            vertices[readIdx++] = point.translation().values()[1];
            if (point.pointType() == straight) {
                // radius
                vertices[readIdx++] = ((StraightPathPoint) point).radius;
            } else {
                // in/out
                CubicPathPoint cubicPoint = (CubicPathPoint) point;
                vertices[readIdx++] = cubicPoint.inPoint().values()[0];
                vertices[readIdx++] = cubicPoint.inPoint().values()[1];
                vertices[readIdx++] = cubicPoint.outPoint().values()[0];
                vertices[readIdx++] = cubicPoint.outPoint().values()[1];
            }
        }
        vertexDeform = vertices;
    }

    public void markVertexDeformDirty() {
        if (artboard == null) {
            return;
        }
        artboard.addDirt(this, vertexDeformDirty, false);
    }

    @Override
    public void update(int dirt) {
        if (vertexDeform != null && (dirt & vertexDeformDirty) == vertexDeformDirty) {
            int readIdx = 0;
            for (final PathPoint point : _points) {
                point.translation().values()[0] = vertexDeform[readIdx++];
                point.translation().values()[1] = vertexDeform[readIdx++];
                switch (point.pointType()) {
                case straight:
                    ((StraightPathPoint) point).radius = vertexDeform[readIdx++];
                    break;

                default:
                    CubicPathPoint cubicPoint = (CubicPathPoint) point;
                    cubicPoint.inPoint().values()[0] = vertexDeform[readIdx++];
                    cubicPoint.inPoint().values()[1] = vertexDeform[readIdx++];
                    cubicPoint.outPoint().values()[0] = vertexDeform[readIdx++];
                    cubicPoint.outPoint().values()[1] = vertexDeform[readIdx++];
                    break;
                }
            }
        }
        invalidateDrawable();

        super.update(dirt);
    }

    public static ActorPath read(ActorArtboard artboard, StreamReader reader, ActorPath component) {
        component = component != null ? component : new ActorPath();
        ActorNode.read(artboard, reader, component);
        ActorSkinnable.read(artboard, reader, component);

        component._isHidden = !reader.readBoolean("isVisible");
        component._isClosed = reader.readBoolean("isClosed");

        reader.openArray("points");
        int pointCount = reader.readUint16Length();
        component._points = new ArrayList<PathPoint>(pointCount);
        for (int i = 0; i < pointCount; i++) {
            reader.openObject("point");
            PathPoint point;
            PointType type = PointType.values()[reader.readUint8("pointType")];
            if (type == straight) {
                point = new StraightPathPoint();
            } else {
                point = new CubicPathPoint(type);
            }
            point.read(reader, component.isConnectedToBones());
            reader.closeObject();

            component._points.add(point);
        }
        reader.closeArray();
        return component;
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorPath instanceEvent = new ActorPath();
        instanceEvent.copyPath(this, resetArtboard);
        return instanceEvent;
    }

    @Override
    public void resolveComponentIndices(List<ActorComponent> components) {
        super.resolveComponentIndices(components);
        resolveSkinnable(components);
    }

    @Override
    public void completeResolve() {
        ActorBasePath.super.completeResolve();
    }

    public void copyPath(ActorPath node, ActorArtboard resetArtboard) {
        copyNode(node, resetArtboard);
        copySkinnable(node, resetArtboard);
        _isHidden = node._isHidden;
        _isClosed = node._isClosed;

        int pointCount = node._points.size();

        _points = new ArrayList<PathPoint>(pointCount);
        for (int i = 0; i < pointCount; i++) {
            _points.add(node._points.get(i).makeInstance());
        }

        if (node.vertexDeform != null) {
            vertexDeform = Arrays.copyOf(node.vertexDeform, node.vertexDeform.length);
        }
    }
}
