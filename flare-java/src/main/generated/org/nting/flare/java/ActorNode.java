package org.nting.flare.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class ActorNode extends ActorComponent {

    public interface ComopnentWalkCallback extends Function<ActorComponent, Boolean> {
    }

    private List<ActorComponent> _children;

    // List<ActorNode> m_Dependents;
    private Mat2D _transform = new Mat2D();
    private Mat2D _worldTransform = new Mat2D();

    private Vec2D _translation = new Vec2D();
    private float _rotation = 0.0f;
    private Vec2D _scale = new Vec2D(1.0f, 1.0f);
    private float _opacity = 1.0f;
    private float _renderOpacity = 1.0f;
    private ActorLayerEffectRenderer _layerEffect;

    public ActorLayerEffectRenderer layerEffect() {
        return _layerEffect;
    }

    private boolean _overrideWorldTransform = false;
    private boolean _isCollapsedVisibility = false;

    private boolean _renderCollapsed = false;
    private List<ActorClip> _clips;

    private List<ActorConstraint> _constraints;
    private List<ActorConstraint> _peerConstraints;

    public static final int transformDirty = DirtyFlags.transformDirty;
    public static final int worldTransformDirty = DirtyFlags.worldTransformDirty;

    public ActorNode() {
    }

    public ActorNode(ActorArtboard artboard) {
        super(artboard);
    }

    public Mat2D transform() {
        return _transform;
    }

    public List<ActorClip> clips() {
        return _clips;
    }

    public Mat2D worldTransformOverride() {
        return _overrideWorldTransform ? _worldTransform : null;
    }

    public void worldTransformOverride(Mat2D value) {
        if (value == null) {
            _overrideWorldTransform = false;
        } else {
            _overrideWorldTransform = true;
            Mat2D.copy(worldTransform(), value);
        }
        markTransformDirty();
    }

    public Mat2D worldTransform() {
        return _worldTransform;
    }

    // N.B. this should only be done if you really know what you're doing.
    // Generally you want to manipulate the local translation, rotation,
    // and scale of a Node.
    public void worldTransform(Mat2D value) {
        Mat2D.copy(_worldTransform, value);
    }

    public double x() {
        return _translation.values()[0];
    }

    public void x(float value) {
        if (_translation.values()[0] == value) {
            return;
        }
        _translation.values()[0] = value;
        markTransformDirty();
    }

    public double y() {
        return _translation.values()[1];
    }

    public void y(float value) {
        if (_translation.values()[1] == value) {
            return;
        }
        _translation.values()[1] = value;
        markTransformDirty();
    }

    public Vec2D translation() {
        return new Vec2D(_translation);
    }

    public void translation(Vec2D value) {
        Vec2D.copy(_translation, value);
        markTransformDirty();
    }

    public float rotation() {
        return _rotation;
    }

    public void rotation(float value) {
        if (_rotation == value) {
            return;
        }
        _rotation = value;
        markTransformDirty();
    }

    public float scaleX() {
        return _scale.values()[0];
    }

    public void scaleX(float value) {
        if (_scale.values()[0] == value) {
            return;
        }
        _scale.values()[0] = value;
        markTransformDirty();
    }

    public double scaleY() {
        return _scale.values()[1];
    }

    public void scaleY(float value) {
        if (_scale.values()[1] == value) {
            return;
        }
        _scale.values()[1] = value;
        markTransformDirty();
    }

    public float opacity() {
        return _opacity;
    }

    public void opacity(float value) {
        if (_opacity == value) {
            return;
        }
        _opacity = value;
        markTransformDirty();
    }

    public float renderOpacity() {
        return _renderOpacity;
    }

    public double childOpacity() {
        return _layerEffect == null ? _renderOpacity : 1;
    }

    // Helper that looks for layer effect, this is only called by
    // ActorLayerEffectRenderer when the parent changes. This keeps it efficient
    // so not every ActorNode has to look for layerEffects as most won't have it.
    public void findLayerEffect() {
        List<ActorLayerEffectRenderer> layerEffects = Optional.ofNullable(children()).orElse(Collections.emptyList())
                .stream().filter(c -> c instanceof ActorLayerEffectRenderer).map(c -> (ActorLayerEffectRenderer) c)
                .collect(Collectors.toList());
        ActorLayerEffectRenderer change = !layerEffects.isEmpty() ? layerEffects.get(0) : null;
        if (_layerEffect != change) {
            _layerEffect = change;
            // Force update the opacity.
            markTransformDirty();
        }
    }

    public boolean renderCollapsed() {
        return _renderCollapsed;
    }

    public boolean collapsedVisibility() {
        return _isCollapsedVisibility;
    }

    public void collapsedVisibility(boolean value) {
        if (_isCollapsedVisibility != value) {
            _isCollapsedVisibility = value;
            markTransformDirty();
        }
    }

    public List<List<ActorClip>> allClips() {
        // Find clips.
        List<List<ActorClip>> all = new ArrayList<List<ActorClip>>();
        ActorNode clipSearch = this;
        while (clipSearch != null) {
            if (clipSearch.clips() != null) {
                all.add(clipSearch.clips());
            }
            clipSearch = clipSearch.parent();
        }

        return all;
    }

    public void markTransformDirty() {
        if (artboard == null) {
            // Still loading?
            return;
        }
        if (!artboard.addDirt(this, transformDirty, false)) {
            return;
        }
        artboard.addDirt(this, worldTransformDirty, true);
    }

    public void updateTransform() {
        Mat2D.fromRotation(_transform, _rotation);
        _transform.values()[4] = _translation.values()[0];
        _transform.values()[5] = _translation.values()[1];
        Mat2D.scale(_transform, _transform, _scale);
    }

    public Vec2D getWorldTranslation(Vec2D vec) {
        vec.values()[0] = _worldTransform.values()[4];
        vec.values()[1] = _worldTransform.values()[5];
        return vec;
    }

    public void updateWorldTransform() {
        _renderOpacity = _opacity;

        if (parent() != null) {
            _renderCollapsed = _isCollapsedVisibility || parent()._renderCollapsed;
            _renderOpacity *= parent().childOpacity();
            if (!_overrideWorldTransform) {
                Mat2D.multiply(_worldTransform, parent()._worldTransform, _transform);
            }
        } else {
            Mat2D.copy(_worldTransform, _transform);
        }
    }

    public static ActorNode read(ActorArtboard artboard, StreamReader reader, ActorNode node) {
        node = node != null ? node : new ActorNode();
        ActorComponent.read(artboard, reader, node);
        Vec2D.copyFromList(node._translation, reader.readFloat32Array(2, "translation"));
        node._rotation = reader.readFloat32("rotation");
        Vec2D.copyFromList(node._scale, reader.readFloat32Array(2, "scale"));
        node._opacity = reader.readFloat32("opacity");
        node._isCollapsedVisibility = reader.readBoolean("isCollapsed");

        reader.openArray("clips");
        int clipCount = reader.readUint8Length();
        if (clipCount > 0) {
            node._clips = new ArrayList<ActorClip>(clipCount);
            for (int i = 0; i < clipCount; i++) {
                reader.openObject("clip");
                ActorClip clip = new ActorClip(reader.readId("node"));
                if (artboard.actor().version() >= 23) {
                    clip.intersect = reader.readBoolean("intersect");
                }
                reader.closeObject();
                node._clips.add(clip);
            }
        }
        reader.closeArray();
        return node;
    }

    public void removeChild(ActorComponent component) {
        Optional.ofNullable(_children).ifPresent(v -> v.remove(component));
    }

    public void addChild(ActorComponent component) {
        if (component.parent() != null) {
            component.parent().removeChild(component);
        }
        component.parent(this);
        _children = _children != null ? _children : new ArrayList<>();
        _children.add(component);
    }

    public List<ActorComponent> children() {
        return _children;
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorNode instanceNode = new ActorNode();
        instanceNode.copyNode(this, resetArtboard);
        return instanceNode;
    }

    public void copyNode(ActorNode node, ActorArtboard resetArtboard) {
        copyComponent(node, resetArtboard);
        _transform = new Mat2D(node._transform);
        _worldTransform = new Mat2D(node._worldTransform);
        _translation = new Vec2D(node._translation);
        _scale = new Vec2D(node._scale);
        _rotation = node._rotation;
        _opacity = node._opacity;
        _renderOpacity = node._renderOpacity;
        _overrideWorldTransform = node._overrideWorldTransform;

        if (node._clips != null) {
            _clips = new ArrayList<ActorClip>(node._clips.size());
            for (int i = 0, l = node._clips.size(); i < l; i++) {
                _clips.add(new ActorClip(node._clips.get(i)));
            }
        } else {
            _clips = null;
        }
    }

    @Override
    public void onDirty(int dirt) {
    }

    public boolean addConstraint(ActorConstraint constraint) {
        _constraints = _constraints != null ? _constraints : new ArrayList<>();
        if (_constraints.contains(constraint)) {
            return false;
        }
        _constraints.add(constraint);
        return true;
    }

    public boolean addPeerConstraint(ActorConstraint constraint) {
        _peerConstraints = _peerConstraints != null ? _peerConstraints : new ArrayList<>();
        if (_peerConstraints.contains(constraint)) {
            return false;
        }
        _peerConstraints.add(constraint);
        return true;
    }

    public List<ActorConstraint> allConstraints() {
        return Optional.ofNullable(_constraints == null ? _peerConstraints
                : _peerConstraints == null ? _constraints
                        : Stream.of(_constraints, _peerConstraints).flatMap(List::stream).collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    @Override
    public void update(int dirt) {
        if ((dirt & transformDirty) == transformDirty) {
            updateTransform();
        }
        if ((dirt & worldTransformDirty) == worldTransformDirty) {
            updateWorldTransform();
            if (_constraints != null) {
                for (final ActorConstraint constraint : _constraints) {
                    if (constraint.isEnabled()) {
                        constraint.constrain(this);
                    }
                }
            }
        }
    }

    @Override
    public void resolveComponentIndices(List<ActorComponent> components) {
        super.resolveComponentIndices(components);

        if (_clips == null) {
            return;
        }

        for (final ActorClip clip : _clips) {
            final ActorComponent component = components.get(clip.clipIdx);
            if (component instanceof ActorNode) {
                clip.node = (ActorNode) component;
            }
        }
    }

    @Override
    public void completeResolve() {
        // Nothing to complete for actornode.
    }

    public boolean eachChildRecursive(ComopnentWalkCallback cb) {
        if (_children != null) {
            for (final ActorComponent child : _children) {
                if (!cb.apply(child)) {
                    return false;
                }

                if (child instanceof ActorNode && !((ActorNode) child).eachChildRecursive(cb)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean all(ComopnentWalkCallback cb) {
        if (!cb.apply(this)) {
            return false;
        }

        if (_children != null) {
            for (final ActorComponent child : _children) {
                if (!cb.apply(child)) {
                    return false;
                }

                if (child instanceof ActorNode) {
                    ((ActorNode) child).eachChildRecursive(cb);
                }
            }
        }

        return true;
    }

    public void invalidateShape() {
    }
}
