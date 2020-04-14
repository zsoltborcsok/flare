package org.nting.flare.java;

import java.util.ArrayList;
import java.util.List;

typedef boolean ComopnentWalkCallback(ActorComponent component);

public class ActorClip {
  int clipIdx;
  boolean intersect = true;
  ActorNode node;

  ActorClip(this.clipIdx);

  ActorClip.copy(ActorClip from)
      : clipIdx = from.clipIdx,
        intersect = from.intersect;
}

public class ActorNode extends ActorComponent {
  List<ActorComponent> _children;

  //List<ActorNode> m_Dependents;
  Mat2D _transform = new Mat2D();
  Mat2D _worldTransform = new Mat2D();

  Vec2D _translation = new Vec2D();
  double _rotation = 0.0;
  Vec2D _scale = Vec2D.fromValues(1.0, 1.0);
  double _opacity = 1.0;
  double _renderOpacity = 1.0;
  ActorLayerEffectRenderer _layerEffect;

  ActorLayerEffectRenderer get layerEffect => _layerEffect;

  boolean _overrideWorldTransform = false;
  boolean _isCollapsedVisibility = false;

  boolean _renderCollapsed = false;
  List<ActorClip> _clips;

  List<ActorConstraint> _constraints;
  List<ActorConstraint> _peerConstraints;

  static const int transformDirty = DirtyFlags.transformDirty;
  static const int worldTransformDirty = DirtyFlags.worldTransformDirty;

  ActorNode();

  ActorNode.withArtboard(ActorArtboard artboard) : super.withArtboard(artboard);

  Mat2D get transform {
    return _transform;
  }

  List<ActorClip> get clips {
    return _clips;
  }

  Mat2D get worldTransformOverride {
    return _overrideWorldTransform ? _worldTransform : null;
  }

  set worldTransformOverride(Mat2D value) {
    if (value == null) {
      _overrideWorldTransform = false;
    } else {
      _overrideWorldTransform = true;
      Mat2D.copy(worldTransform, value);
    }
    markTransformDirty();
  }

  Mat2D get worldTransform {
    return _worldTransform;
  }

  // N.B. this should only be done if you really know what you're doing.
  // Generally you want to manipulate the local translation, rotation,
  // and scale of a Node.
  set worldTransform(Mat2D value) {
    Mat2D.copy(_worldTransform, value);
  }

  double get x {
    return _translation[0];
  }

  set x(double value) {
    if (_translation[0] == value) {
      return;
    }
    _translation[0] = value;
    markTransformDirty();
  }

  double get y {
    return _translation[1];
  }

  set y(double value) {
    if (_translation[1] == value) {
      return;
    }
    _translation[1] = value;
    markTransformDirty();
  }

  Vec2D get translation {
    return Vec2D.clone(_translation);
  }

  set translation(Vec2D value) {
    Vec2D.copy(_translation, value);
    markTransformDirty();
  }

  double get rotation {
    return _rotation;
  }

  set rotation(double value) {
    if (_rotation == value) {
      return;
    }
    _rotation = value;
    markTransformDirty();
  }

  double get scaleX {
    return _scale[0];
  }

  set scaleX(double value) {
    if (_scale[0] == value) {
      return;
    }
    _scale[0] = value;
    markTransformDirty();
  }

  double get scaleY {
    return _scale[1];
  }

  set scaleY(double value) {
    if (_scale[1] == value) {
      return;
    }
    _scale[1] = value;
    markTransformDirty();
  }

  double get opacity {
    return _opacity;
  }

  set opacity(double value) {
    if (_opacity == value) {
      return;
    }
    _opacity = value;
    markTransformDirty();
  }

  double get renderOpacity {
    return _renderOpacity;
  }

  double get childOpacity {
    return _layerEffect == null ? _renderOpacity : 1;
  }

  // Helper that looks for layer effect, this is only called by
  // ActorLayerEffectRenderer when the parent changes. This keeps it efficient
  // so not every ActorNode has to look for layerEffects as most won't have it.
  public void findLayerEffect() {
    var layerEffects = children?.whereType<ActorLayerEffectRenderer>();
    var change = layerEffects != null && layerEffects.isNotEmpty
        ? layerEffects.first
        : null;
    if (_layerEffect != change) {
      _layerEffect = change;
      // Force update the opacity.
      markTransformDirty();
    }
  }

  boolean get renderCollapsed {
    return _renderCollapsed;
  }

  boolean get collapsedVisibility {
    return _isCollapsedVisibility;
  }

  set collapsedVisibility(boolean value) {
    if (_isCollapsedVisibility != value) {
      _isCollapsedVisibility = value;
      markTransformDirty();
    }
  }

  List<List<ActorClip>> get allClips {
    // Find clips.
    List<List<ActorClip>> all = <List<ActorClip>>[];
    ActorNode clipSearch = this;
    while (clipSearch != null) {
      if (clipSearch.clips != null) {
        all.add(clipSearch.clips);
      }
      clipSearch = clipSearch.parent;
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
    _transform[4] = _translation[0];
    _transform[5] = _translation[1];
    Mat2D.scale(_transform, _transform, _scale);
  }

  public Vec2D getWorldTranslation(Vec2D vec) {
    vec[0] = _worldTransform[4];
    vec[1] = _worldTransform[5];
    return vec;
  }

  public void updateWorldTransform() {
    _renderOpacity = _opacity;

    if (parent != null) {
      _renderCollapsed = _isCollapsedVisibility || parent._renderCollapsed;
      _renderOpacity *= parent.childOpacity;
      if (!_overrideWorldTransform) {
        Mat2D.multiply(_worldTransform, parent._worldTransform, _transform);
      }
    } else {
      Mat2D.copy(_worldTransform, _transform);
    }
  }

  static ActorNode read(ActorArtboard artboard, StreamReader reader,
      ActorNode node) {
    node ??= new ActorNode();
    ActorComponent.read(artboard, reader, node);
    Vec2D.copyFromList(
        node._translation, reader.readFloat32Array(2, "translation"));
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
        var clip = new ActorClip(reader.readId("node"));
        if (artboard.actor.version >= 23) {
          clip.intersect = reader.readBoolean("intersect");
        }
        reader.closeObject();
        node._clips[i] = clip;
      }
    }
    reader.closeArray();
    return node;
  }

  public void removeChild(ActorComponent component) {
    _children?.remove(component);
  }

  public void addChild(ActorComponent component) {
    if (component.parent != null) {
      component.parent.removeChild(component);
    }
    component.parent = this;
    _children ??= <ActorComponent>[];
    _children.add(component);
  }

  List<ActorComponent> get children {
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
    _transform = Mat2D.clone(node._transform);
    _worldTransform = Mat2D.clone(node._worldTransform);
    _translation = Vec2D.clone(node._translation);
    _scale = Vec2D.clone(node._scale);
    _rotation = node._rotation;
    _opacity = node._opacity;
    _renderOpacity = node._renderOpacity;
    _overrideWorldTransform = node._overrideWorldTransform;

    if (node._clips != null) {
      _clips = new ArrayList<ActorClip>(node._clips.length);
      for (int i = 0, l = node._clips.length; i < l; i++) {
        _clips[i] = ActorClip.copy(node._clips[i]);
      }
    } else {
      _clips = null;
    }
  }

  @Override
  public void onDirty(int dirt) {}

  public boolean addConstraint(ActorConstraint constraint) {
    _constraints ??= <ActorConstraint>[];
    if (_constraints.contains(constraint)) {
      return false;
    }
    _constraints.add(constraint);
    return true;
  }

  public boolean addPeerConstraint(ActorConstraint constraint) {
    _peerConstraints ??= <ActorConstraint>[];
    if (_peerConstraints.contains(constraint)) {
      return false;
    }
    _peerConstraints.add(constraint);
    return true;
  }

  List<ActorConstraint> get allConstraints =>
      (_constraints == null
          ? _peerConstraints
          : _peerConstraints == null
          ? _constraints
          : _constraints + _peerConstraints) ??
          <ActorConstraint>[];

  @Override
  public void update(int dirt) {
    if ((dirt & transformDirty) == transformDirty) {
      updateTransform();
    }
    if ((dirt & worldTransformDirty) == worldTransformDirty) {
      updateWorldTransform();
      if (_constraints != null) {
        for (final ActorConstraint constraint : _constraints) {
          if (constraint.isEnabled) {
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
      final ActorComponent component = components[clip.clipIdx];
      if (component is ActorNode) {
        clip.node = component;
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
        if (cb(child) == false) {
          return false;
        }

        if (child is ActorNode && child.eachChildRecursive(cb) == false) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean all(ComopnentWalkCallback cb) {
    if (cb(this) == false) {
      return false;
    }

    if (_children != null) {
      for (final ActorComponent child : _children) {
        if (cb(child) == false) {
          return false;
        }

        if (child is ActorNode) {
          child.eachChildRecursive(cb);
        }
      }
    }

    return true;
  }

  public void invalidateShape() {}
}
