package org.nting.flare.java;

enum MaskType { alpha, invertedAlpha, luminance, invertedLuminance }

HashMap<int, MaskType> maskTypeLookup = HashMap<int, MaskType>.fromIterables([
  0,
  1,
  2,
  3
], [
  MaskType.alpha,
  MaskType.invertedAlpha,
  MaskType.luminance,
  MaskType.invertedLuminance
]);

public class ActorMask extends ActorLayerEffect {
  ActorNode _source;
  int _sourceIdx;
  MaskType _maskType;

  ActorNode get source => _source;

  MaskType get maskType => _maskType;

  static ActorMask read(ActorArtboard artboard, StreamReader reader,
      ActorMask component) {
    component ??= new ActorMask();
    ActorLayerEffect.read(artboard, reader, component);
    component._sourceIdx = reader.readId("source");
    component._maskType =
        maskTypeLookup[reader.readUint8("maskType")] ?? MaskType.alpha;

    return component;
  }

  void copyMask(ActorMask from, ActorArtboard resetArtboard) {
    copyLayerEffect(from, resetArtboard);
    _sourceIdx = from._sourceIdx;
    _maskType = from._maskType;
  }

  @override
  void resolveComponentIndices(List<ActorComponent> components) {
    super.resolveComponentIndices(components);

    _source = components[_sourceIdx] as ActorNode;
  }

  @override
  void completeResolve() {
    // intentionally empty, no logic to complete.
  }

  @override
  ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorMask instanceNode = new ActorMask();
    instanceNode.copyMask(this, resetArtboard);
    return instanceNode;
  }

  @override
  void onDirty(int dirt) {
    // intentionally empty
  }

  @override
  void update(int dirt) {
    // intentionally empty
  }
}
