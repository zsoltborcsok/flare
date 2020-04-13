package org.nting.flare.java;

public class ActorEvent extends ActorComponent {
  static ActorComponent read(ActorArtboard artboard, StreamReader reader,
      ActorEvent component) {
    component ??= new ActorEvent();

    ActorComponent.read(artboard, reader, component);

    return component;
  }

  @override
  ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorEvent instanceEvent = new ActorEvent();
    instanceEvent.copyComponent(this, resetArtboard);
    return instanceEvent;
  }

  @override
  void completeResolve() {}

  @override
  void onDirty(int dirt) {}

  @override
  void update(int dirt) {}
}
