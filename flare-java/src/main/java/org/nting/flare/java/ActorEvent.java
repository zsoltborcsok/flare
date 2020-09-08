package org.nting.flare.java;

public class ActorEvent extends ActorComponent {

    public static ActorComponent read(ActorArtboard artboard, StreamReader reader, ActorEvent component) {
        component = component != null ? component : new ActorEvent();

        ActorComponent.read(artboard, reader, component);

        return component;
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorEvent instanceEvent = new ActorEvent();
        instanceEvent.copyComponent(this, resetArtboard);
        return instanceEvent;
    }

    @Override
    public void completeResolve() {
    }

    @Override
    public void onDirty(int dirt) {
    }

    @Override
    public void update(int dirt) {
    }
}
