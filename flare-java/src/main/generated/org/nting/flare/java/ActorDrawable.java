package org.nting.flare.java;

import org.nting.flare.java.maths.AABB;

import java.util.List;

public abstract class ActorDrawable extends ActorNode {

  enum BlendModes { normal, multiply, screen, additive }

  public static class ClipShape {
    final ActorShape shape;
    final boolean intersect;

    ClipShape(this.shape, this.intersect);
  }

  private List<List<ClipShape>> _clipShapes;

  public List<List<ClipShape>> clipShapes() { return _clipShapes; }

  // Editor set draw index.
  private int _drawOrder;

  public int drawOrder() { return _drawOrder; }

  set drawOrder(int value) {
    if (_drawOrder == value) {
      return;
    }
    _drawOrder = value;
    artboard.markDrawOrderDirty();
  }

  // Computed draw index in the draw list.
  public int drawIndex;
  public boolean isHidden;

  public boolean doesDraw() {
    return !isHidden && !renderCollapsed;
  }

  public abstract int blendModeId();

  public abstract set blendModeId(int value);

  static ActorDrawable read(ActorArtboard artboard, StreamReader reader,
      ActorDrawable component) {
    ActorNode.read(artboard, reader, component);

    component.isHidden = !reader.readBoolean("isVisible");
    if (artboard.actor.version < 21) {
      component.blendModeId = 3;
    } else {
      component.blendModeId = reader.readUint8("blendMode");
    }
    component.drawOrder = reader.readUint16("drawOrder");

    return component;
  }

  public void copyDrawable(ActorDrawable node, ActorArtboard resetArtboard) {
    copyNode(node, resetArtboard);
    // todo blendmode
    drawOrder = node.drawOrder;
    blendModeId = node.blendModeId;
    isHidden = node.isHidden;
  }

  public abstract AABB computeAABB();

  public void initializeGraphics() {}

  @Override
  public void completeResolve() {
    _clipShapes = <List<ClipShape>>[];
    List<List<ActorClip>> clippers = allClips;
    for (final List<ActorClip> clips : clippers) {
      List<ClipShape> shapes = <ClipShape>[];
      for (final ActorClip clip : clips) {
        clip.node.all((component) {
          if (component instanceof ActorShape) {
            shapes.add(ClipShape(component, clip.intersect));
          }
          return true;
        });
      }
      if (shapes.isNotEmpty) {
        _clipShapes.add(shapes);
      }
    }
  }

  /// If this is set the drawable belongs to a layer. We store a reference to
  /// the parent node that contains the layer.
  public ActorNode layerEffectRenderParent;
}
