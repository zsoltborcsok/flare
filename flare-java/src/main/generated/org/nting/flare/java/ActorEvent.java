package org.nting.flare.java;

class ActorEvent extends ActorComponent {
  static ActorComponent read(ActorArtboard artboard, StreamReader reader,
      ActorEvent component) {
    component ??= ActorEvent();

    ActorComponent.read(artboard, reader, component);

    return component;
  }

  @override
  ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorEvent instanceEvent = ActorEvent();
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
