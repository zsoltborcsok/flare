package org.nting.flare.java;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.List;

public class ActorNodeSolo extends ActorNode {

    private int _activeChildIndex = 0;

    public void activeChildIndex(int idx) {
        if (idx != _activeChildIndex) {
            setActiveChildIndex(idx);
        }
    }

    public int activeChildIndex() {
        return _activeChildIndex;
    }

    public void setActiveChildIndex(int idx) {
        List<ActorComponent> children = children();
        if (children != null) {
            _activeChildIndex = min(children.size(), max(0, idx));
            for (int i = 0; i < children.size(); i++) {
                ActorComponent child = children.get(i);
                boolean cv = i != (_activeChildIndex - 1);
                if (child instanceof ActorNode) {
                    ((ActorNode) child).collapsedVisibility(cv); // Setter
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

    public static ActorNodeSolo read(ActorArtboard artboard, StreamReader reader, ActorNodeSolo node) {
        node = node != null ? node : new ActorNodeSolo();

        ActorNode.read(artboard, reader, node);
        node._activeChildIndex = reader.readInt32("activeChild");
        return node;
    }

    @Override
    public void completeResolve() {
        super.completeResolve();
        setActiveChildIndex(activeChildIndex());
    }
}
