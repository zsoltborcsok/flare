package org.nting.flare.java;

import java.util.List;

public abstract class ActorComponent {

    private String _name = "Unnamed";
    private ActorNode _parent;

    public ActorArtboard artboard;
    private int _parentIdx = 0;
    public int idx = 0;
    public int graphOrder = 0;
    public int dirtMask = 0;
    public List<ActorComponent> dependents;

    public ActorComponent() {
    }

    public ActorComponent(ActorArtboard artboard) {
        this.artboard = artboard;
    }

    public String name() {
        return _name;
    }

    public ActorNode parent() {
        return _parent;
    }

    public void parent(ActorNode value) {
        if (_parent == value) {
            return;
        }
        ActorNode from = _parent;
        _parent = value;
        onParentChanged(from, value);
    }

    public void onParentChanged(ActorNode from, ActorNode to) {
    }

    public void resolveComponentIndices(List<ActorComponent> components) {
        ActorNode node = (ActorNode) components.get(_parentIdx);
        if (node != null) {
            node.addChild(this);
            artboard.addDependency(this, node);
        }
    }

    public abstract void completeResolve();

    public abstract ActorComponent makeInstance(ActorArtboard resetArtboard);

    public abstract void onDirty(int dirt);

    public abstract void update(int dirt);

    public static ActorComponent read(ActorArtboard artboard, StreamReader reader, ActorComponent component) {
        component.artboard = artboard;
        component._name = reader.readString("name");
        component._parentIdx = reader.readId("parent");

        return component;
    }

    public void copyComponent(ActorComponent component, ActorArtboard resetArtboard) {
        _name = component._name;
        artboard = resetArtboard;
        _parentIdx = component._parentIdx;
        idx = component.idx;
    }
}
