package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.TransformComponents;

public class ActorRotationConstraint extends ActorTargetedConstraint {

    public static final double pi2 = Math.PI * 2.0;

    private boolean _copy = false;
    private float _scale = 1.0f;
    private boolean _enableMin = false;
    private boolean _enableMax = false;
    private float _max = (float) pi2;
    private float _min = (float) -pi2;
    private boolean _offset = false;
    private int _sourceSpace = TransformSpace.world;
    private int _destSpace = TransformSpace.world;
    private int _minMaxSpace = TransformSpace.world;
    private final TransformComponents _componentsA = new TransformComponents();
    private final TransformComponents _componentsB = new TransformComponents();

    public static ActorRotationConstraint read(ActorArtboard artboard, StreamReader reader,
            ActorRotationConstraint component) {
        component = component != null ? component : new ActorRotationConstraint();
        ActorTargetedConstraint.read(artboard, reader, component);
        component._copy = reader.readBoolean("copy");
        if (component._copy) {
            component._scale = reader.readFloat32("scale");
        }
        component._enableMin = reader.readBoolean("enableMin");
        if (component._enableMin) {
            component._min = reader.readFloat32("min");
        }
        component._enableMax = reader.readBoolean("enableMax");
        if (component._enableMax) {
            component._max = reader.readFloat32("max");
        }

        component._offset = reader.readBoolean("offset");
        component._sourceSpace = reader.readUint8("sourceSpaceId");
        component._destSpace = reader.readUint8("destSpaceId");
        component._minMaxSpace = reader.readUint8("minMaxSpaceId");

        return component;
    }

    @Override
    public void constrain(ActorNode node) {
        ActorNode target = (ActorNode) this.target();
        ActorNode grandParent = parent().parent();

        Mat2D transformA = parent().worldTransform();
        Mat2D transformB = new Mat2D();
        Mat2D.decompose(transformA, _componentsA);
        if (target == null) {
            Mat2D.copy(transformB, transformA);
            _componentsB.values()[0] = _componentsA.values()[0];
            _componentsB.values()[1] = _componentsA.values()[1];
            _componentsB.values()[2] = _componentsA.values()[2];
            _componentsB.values()[3] = _componentsA.values()[3];
            _componentsB.values()[4] = _componentsA.values()[4];
            _componentsB.values()[5] = _componentsA.values()[5];
        } else {
            Mat2D.copy(transformB, target.worldTransform());
            if (_sourceSpace == TransformSpace.local) {
                ActorNode sourceGrandParent = target.parent();
                if (sourceGrandParent != null) {
                    Mat2D inverse = new Mat2D();
                    if (!Mat2D.invert(inverse, sourceGrandParent.worldTransform())) {
                        return;
                    }
                    Mat2D.multiply(transformB, inverse, transformB);
                }
            }
            Mat2D.decompose(transformB, _componentsB);

            if (!_copy) {
                _componentsB.rotation(_destSpace == TransformSpace.local ? 1.0f : _componentsA.rotation());
            } else {
                _componentsB.rotation(_componentsB.rotation() * _scale);
                if (_offset) {
                    _componentsB.rotation(_componentsB.rotation() + parent().rotation());
                }
            }

            if (_destSpace == TransformSpace.local) {
                // Destination space is in parent transform coordinates.
                // Recompose the parent local transform and get it in world,
                // then decompose the world for interpolation.
                if (grandParent != null) {
                    Mat2D.compose(transformB, _componentsB);
                    Mat2D.multiply(transformB, grandParent.worldTransform(), transformB);
                    Mat2D.decompose(transformB, _componentsB);
                }
            }
        }

        boolean clampLocal = _minMaxSpace == TransformSpace.local && grandParent != null;
        if (clampLocal) {
            // Apply min max in local space, so transform to local coordinates first.
            Mat2D.compose(transformB, _componentsB);
            Mat2D inverse = new Mat2D();
            if (!Mat2D.invert(inverse, grandParent.worldTransform())) {
                return;
            }
            Mat2D.multiply(transformB, inverse, transformB);
            Mat2D.decompose(transformB, _componentsB);
        }
        if (_enableMax && _componentsB.rotation() > _max) {
            _componentsB.rotation(_max);
        }
        if (_enableMin && _componentsB.rotation() < _min) {
            _componentsB.rotation(_min);
        }
        if (clampLocal) {
            // Transform back to world.
            Mat2D.compose(transformB, _componentsB);
            Mat2D.multiply(transformB, grandParent.worldTransform(), transformB);
            Mat2D.decompose(transformB, _componentsB);
        }

        float angleA = (float) (_componentsA.rotation() % pi2);
        float angleB = (float) (_componentsB.rotation() % pi2);
        float diff = angleB - angleA;

        if (diff > Math.PI) {
            diff -= pi2;
        } else if (diff < -Math.PI) {
            diff += pi2;
        }
        _componentsB.rotation(_componentsA.rotation() + diff * strength());
        _componentsB.x(_componentsA.x());
        _componentsB.y(_componentsA.y());
        _componentsB.scaleX(_componentsA.scaleX());
        _componentsB.scaleY(_componentsA.scaleY());
        _componentsB.skew(_componentsA.skew());

        Mat2D.compose(parent().worldTransform(), _componentsB);
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorRotationConstraint instance = new ActorRotationConstraint();
        instance.copyRotationConstraint(this, resetArtboard);
        return instance;
    }

    void copyRotationConstraint(ActorRotationConstraint node, ActorArtboard resetArtboard) {
        copyTargetedConstraint(node, resetArtboard);

        _copy = node._copy;
        _scale = node._scale;
        _enableMin = node._enableMin;
        _enableMax = node._enableMax;
        _min = node._min;
        _max = node._max;

        _offset = node._offset;
        _sourceSpace = node._sourceSpace;
        _destSpace = node._destSpace;
        _minMaxSpace = node._minMaxSpace;
    }

    @Override
    public void update(int dirt) {
    }

    @Override
    public void completeResolve() {
    }
}
