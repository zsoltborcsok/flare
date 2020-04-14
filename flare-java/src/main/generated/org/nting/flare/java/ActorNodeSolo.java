package org.nting.flare.java;

public class ActorNodeSolo extends ActorNode {
  int _activeChildIndex = 0;

  set activeChildIndex(int idx) {
    if (idx != _activeChildIndex) {
      setActiveChildIndex(idx);
    }
  }

  int get activeChildIndex {
    return _activeChildIndex;
  }

  public void setActiveChildIndex(int idx) {
    if (children != null) {
      _activeChildIndex = min(children.length, max(0, idx));
      for (int i = 0; i < children.length; i++) {
        var child = children[i];
        bool cv = i != (_activeChildIndex - 1);
        if (child is ActorNode) {
          child.collapsedVisibility = cv; // Setter
        }
      }
    }
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorNodeSolo soloInstance = new ActorNodeSolo();
    soloInstance.copySolo(this, resetArtboard);
    return soloInstance;
  }

  public void copySolo(ActorNodeSolo node, ActorArtboard resetArtboard) {
    copyNode(node, resetArtboard);
    _activeChildIndex = node._activeChildIndex;
  }

  static ActorNodeSolo read(ActorArtboard artboard, StreamReader reader,
      ActorNodeSolo node) {
    node ??= new ActorNodeSolo();

    ActorNode.read(artboard, reader, node);
    node._activeChildIndex = reader.readUint32("activeChild");
    return node;
  }

  @Override
  public void completeResolve() {
    super.completeResolve();
    setActiveChildIndex(activeChildIndex);
  }
}
