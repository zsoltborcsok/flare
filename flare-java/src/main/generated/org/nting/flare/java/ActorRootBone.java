package org.nting.flare.java;

public class ActorRootBone extends ActorNode {
  ActorBone _firstBone;

  ActorBone get firstBone {
    return _firstBone;
  }

  @Override
  public void completeResolve() {
    super.completeResolve();
    if (children == null) {
      return;
    }
    for (final ActorComponent component in children) {
      if (component is ActorBone) {
        _firstBone = component;
        return;
      }
    }
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorRootBone instanceNode = new ActorRootBone();
    instanceNode.copyNode(this, resetArtboard);
    return instanceNode;
  }

  static ActorRootBone read(ActorArtboard artboard, StreamReader reader,
      ActorRootBone node) {
    node ??= new ActorRootBone();
    ActorNode.read(artboard, reader, node);
    return node;
  }
}
