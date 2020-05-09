package org.nting.flare.java;

public class ActorRootBone extends ActorNode {

    private ActorBone _firstBone;

    public ActorBone firstBone() {
        return _firstBone;
    }

    @Override
    public void completeResolve() {
        super.completeResolve();
        if (children() == null) {
            return;
        }
        for (ActorComponent component : children()) {
            if (component instanceof ActorBone) {
                _firstBone = (ActorBone) component;
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

    public static ActorRootBone read(ActorArtboard artboard, StreamReader reader, ActorRootBone node) {
        node = node != null ? node : new ActorRootBone();
        ActorNode.read(artboard, reader, node);
        return node;
    }
}
