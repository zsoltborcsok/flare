package org.nting.flare.java;

public abstract class ActorConstraint extends ActorComponent {
  bool _isEnabled;
  double _strength;

  bool get isEnabled {
    return _isEnabled;
  }

  set isEnabled(bool value) {
    if (value == _isEnabled) {
      return;
    }
    _isEnabled = value;
    markDirty();
  }

  @Override
  public void onDirty(int dirt) {
    markDirty();
  }

  double get strength {
    return _strength;
  }

  set strength(double value) {
    if (value == _strength) {
      return;
    }
    _strength = value;
    markDirty();
  }

  public void markDirty() {
    parent.markTransformDirty();
  }

  public abstract void constrain(ActorNode node);

  @Override
  public void resolveComponentIndices(List<ActorComponent> components) {
    super.resolveComponentIndices(components);
    if (parent != null) {
      // This works because nodes are exported in hierarchy order, 
      // so we are assured constraints get added in order as we resolve indices.
      parent.addConstraint(this);
    }
  }

  static ActorConstraint read(ActorArtboard artboard, StreamReader reader,
      ActorConstraint component) {
    ActorComponent.read(artboard, reader, component);
    component._strength = reader.readFloat32("strength");
    component._isEnabled = reader.readBool("isEnabled");

    return component;
  }

  public void copyConstraint(ActorConstraint node, ActorArtboard resetArtboard) {
    copyComponent(node, resetArtboard);

    _isEnabled = node._isEnabled;
    _strength = node._strength;
  }
}
