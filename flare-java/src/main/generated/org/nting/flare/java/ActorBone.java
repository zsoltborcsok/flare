package org.nting.flare.java;

public class ActorBone extends ActorBoneBase {
  private ActorBone _firstBone;
  public JellyComponent jelly;

  public ActorBone firstBone() {
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
      if (component instanceof ActorBone) {
        _firstBone = component;
        return;
      }
    }
  }

  static ActorBone read(ActorArtboard artboard, StreamReader reader,
      ActorBone node) {
    node = node != null ? node : new ActorBone();
    ActorBoneBase.read(artboard, reader, node);
    return node;
  }
}
