package org.nting.flare.java;

public class ActorBone extends ActorBoneBase {
  ActorBone _firstBone;
  JellyComponent jelly;

  ActorBone get firstBone {
    return _firstBone;
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorBone instanceNode = new ActorBone();
    instanceNode.copyBoneBase(this, resetArtboard);
    return instanceNode;
  }

  @Override
  public void completeResolve() {
    super.completeResolve();
    if (children == null) {
      return;
    }
    for (final ActorComponent component : children) {
      if (component is ActorBone) {
        _firstBone = component;
        return;
      }
    }
  }

  static ActorBone read(ActorArtboard artboard, StreamReader reader,
      ActorBone node) {
    node ??= new ActorBone();
    ActorBoneBase.read(artboard, reader, node);
    return node;
  }
}
