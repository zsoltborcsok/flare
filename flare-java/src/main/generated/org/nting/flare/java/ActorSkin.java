package org.nting.flare.java;

public class ActorSkin extends ActorComponent {
  Float32List _boneMatrices;

  Float32List get boneMatrices => _boneMatrices;

  @override
  public void onDirty(int dirt) {
    // Intentionally empty. Doesn't throw dirt around.
  }

  @override
  public void update(int dirt) {
    ActorSkinnable skinnable = parent as ActorSkinnable;
    if (skinnable == null) {
      return;
    }

    if (skinnable.isConnectedToBones) {
      List<SkinnedBone> connectedBones = skinnable.connectedBones;
      int length = (connectedBones.length + 1) * 6;
      if (_boneMatrices == null || _boneMatrices.length != length) {
        _boneMatrices = new Float32List(length);
        // First bone transform is always identity.
        _boneMatrices[0] = 1.0;
        _boneMatrices[1] = 0.0;
        _boneMatrices[2] = 0.0;
        _boneMatrices[3] = 1.0;
        _boneMatrices[4] = 0.0;
        _boneMatrices[5] = 0.0;
      }

      int bidx = 6; // Start after first identity.

      Mat2D mat = new Mat2D();

      for (final SkinnedBone cb in connectedBones) {
        if (cb.node == null) {
          _boneMatrices[bidx++] = 1.0;
          _boneMatrices[bidx++] = 0.0;
          _boneMatrices[bidx++] = 0.0;
          _boneMatrices[bidx++] = 1.0;
          _boneMatrices[bidx++] = 0.0;
          _boneMatrices[bidx++] = 0.0;
          continue;
        }

        Mat2D.multiply(mat, cb.node.worldTransform, cb.inverseBind);

        _boneMatrices[bidx++] = mat[0];
        _boneMatrices[bidx++] = mat[1];
        _boneMatrices[bidx++] = mat[2];
        _boneMatrices[bidx++] = mat[3];
        _boneMatrices[bidx++] = mat[4];
        _boneMatrices[bidx++] = mat[5];
      }
    }

    skinnable.invalidateDrawable();
  }

  @override
  public void completeResolve() {
    ActorSkinnable skinnable = parent as ActorSkinnable;
    if (skinnable == null) {
      return;
    }
    skinnable.skin = this;
    artboard.addDependency(this, skinnable as ActorComponent);
    if (skinnable.isConnectedToBones) {
      List<SkinnedBone> connectedBones = skinnable.connectedBones;
      for (final SkinnedBone skinnedBone in connectedBones) {
        artboard.addDependency(this, skinnedBone.node);
        List<ActorConstraint> constraints = skinnedBone.node.allConstraints;

        if (constraints != null) {
          for (final ActorConstraint constraint in constraints) {
            artboard.addDependency(this, constraint);
          }
        }
      }
    }
  }

  @override
  public ActorComponent makeInstance(ActorArtboard resetArtboard) {
    ActorSkin instance = new ActorSkin();
    instance.copyComponent(this, resetArtboard);
    return instance;
  }
}