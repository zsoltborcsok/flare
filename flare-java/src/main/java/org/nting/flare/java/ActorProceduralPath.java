package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;

public abstract class ActorProceduralPath extends ActorNode implements ActorBasePath {

    private ActorShape _shape;
    private boolean _isRootPath = false;

    private float _width;
    private float _height;

    @Override
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

    public float width() {
        return _width;
    }

    public float height() {
        return _height;
    }

    @Override
    public Mat2D pathTransform() {
        return worldTransform();
    }

    public void width(float w) {
        if (w != _width) {
            _width = w;
            invalidateDrawable();
        }
    }

    public void height(float w) {
        if (w != _height) {
            _height = w;
            invalidateDrawable();
        }
    }

    public void copyPath(ActorProceduralPath node, ActorArtboard resetArtboard) {
        copyNode(node, resetArtboard);
        _width = node.width();
        _height = node.height();
    }

    @Override
    public void onDirty(int dirt) {
        super.onDirty(dirt);
        // We transformed, make sure parent is invalidated.
        if (shape() != null) {
            shape().invalidateShape();
        }
    }

    @Override
    public void completeResolve() {
        ActorBasePath.super.completeResolve();
    }
}
