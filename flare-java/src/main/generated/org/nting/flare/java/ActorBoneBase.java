package org.nting.flare.java;

public class ActorBoneBase extends ActorNode {
  double _length;

  double get length {
    return _length;
  }

  set length(double value) {
    if (_length == value) {
      return;
    }
    _length = value;
    if (children == null) {
      return;
    }
    for (final ActorComponent component in children) {
      if (component is ActorBoneBase) {
        component.x = value;
      }
    }
  }

  public Vec2D getTipWorldTranslation(Vec2D vec) {
    Mat2D transform = new Mat2D();
    transform[4] = _length;
    Mat2D.multiply(transform, worldTransform, transform);
    vec[0] = transform[4];
    vec[1] = transform[5];
    return vec;
  }

  static ActorBoneBase read(ActorArtboard artboard, StreamReader reader,
      ActorBoneBase node) {
    ActorNode.read(artboard, reader, node);

    node._length = reader.readFloat32("length");

    return node;
  }

  public void copyBoneBase(ActorBoneBase node, ActorArtboard resetArtboard) {
    super.copyNode(node, resetArtboard);
    _length = node._length;
  }
}