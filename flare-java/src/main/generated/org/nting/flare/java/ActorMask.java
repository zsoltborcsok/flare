package org.nting.flare.java;

import java.util.List;

public class ActorMask extends ActorLayerEffect {

  enum MaskType { alpha, invertedAlpha, luminance, invertedLuminance }

  private ActorNode _source;
  private int _sourceIdx;
  private MaskType _maskType;

  public ActorNode source() { return _source; }

  public MaskType maskType() { return _maskType; }

  static ActorMask read(ActorArtboard artboard, StreamReader reader,
      ActorMask component) {
    component = component != null ? component : new ActorMask();
    ActorLayerEffect.read(artboard, reader, component);
    component._sourceIdx = reader.readId("source");
    component._maskType =
        MaskType.values()[reader.readUint8("maskType")] ?? MaskType.alpha;

    return component;
  }

  public void copyMask(ActorMask from, ActorArtboard resetArtboard) {
    copyLayerEffect(from, resetArtboard);
    _sourceIdx = from._sourceIdx;
    _maskType = from._maskType;
  }

  @Override
  public void resolveComponentIndices(List<ActorComponent> components) {
    super.resolveComponentIndices(components);

    _source = (ActorNode) components[_sourceIdx];
  }

  @Override
  public void completeResolve() {
    // intentionally empty, no logic to complete.
  }

  @Override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorMask instanceNode = new ActorMask();
    instanceNode.copyMask(this, resetArtboard);
    return instanceNode;
  }

  @Override
  public void onDirty(int dirt) {
    // intentionally empty
  }

  @Override
  public void update(int dirt) {
    // intentionally empty
  }
}
