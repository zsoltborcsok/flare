package org.nting.flare.java;

public abstract class ActorPaint extends ActorComponent {
  double _opacity = 1.0;

  double get opacity => _opacity;

  set opacity(double value) {
    if (value == _opacity) {
      return;
    }
    _opacity = value;
    markPaintDirty();
  }

  public void copyPaint(ActorPaint component, ActorArtboard resetArtboard) {
    copyComponent(component, resetArtboard);
    opacity = component.opacity;
  }

  static ActorPaint read(ActorArtboard artboard, StreamReader reader,
      ActorPaint component) {
    ActorComponent.read(artboard, reader, component);
    component.opacity = reader.readFloat32("opacity");

    return component;
  }

  @Override
  public void completeResolve() {
    artboard.addDependency(this, parent);
  }

  ActorShape get shape => parent as ActorShape;

  public void markPaintDirty() {
    artboard.addDirt(this, DirtyFlags.paintDirty, false);
  }
}