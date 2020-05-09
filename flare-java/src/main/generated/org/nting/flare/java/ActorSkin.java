package org.nting.flare.java;

import java.util.List;

import org.nting.flare.java.maths.Mat2D;

public class ActorSkin extends ActorComponent {

    private float[] _boneMatrices;

    public float[] boneMatrices() {
        return _boneMatrices;
    }

    @Override
    public void onDirty(int dirt) {
        // Intentionally empty. Doesn't throw dirt around.
    }

    @Override
    public void update(int dirt) {
        ActorSkinnable skinnable = (ActorSkinnable) parent();
        if (skinnable == null) {
            return;
        }

        if (skinnable.isConnectedToBones()) {
            List<SkinnedBone> connectedBones = skinnable.connectedBones();
            int length = (connectedBones.size() + 1) * 6;
            if (_boneMatrices == null || _boneMatrices.length != length) {
                _boneMatrices = new float[length];
                // First bone transform is always identity.
                _boneMatrices[0] = 1.0f;
                _boneMatrices[1] = 0.0f;
                _boneMatrices[2] = 0.0f;
                _boneMatrices[3] = 1.0f;
                _boneMatrices[4] = 0.0f;
                _boneMatrices[5] = 0.0f;
            }

            int bidx = 6; // Start after first identity.

            Mat2D mat = new Mat2D();

            for (final SkinnedBone cb : connectedBones) {
                if (cb.node == null) {
                    _boneMatrices[bidx++] = 1.0f;
                    _boneMatrices[bidx++] = 0.0f;
                    _boneMatrices[bidx++] = 0.0f;
                    _boneMatrices[bidx++] = 1.0f;
                    _boneMatrices[bidx++] = 0.0f;
                    _boneMatrices[bidx++] = 0.0f;
                    continue;
                }

                Mat2D.multiply(mat, cb.node.worldTransform(), cb.inverseBind);

                _boneMatrices[bidx++] = mat.values()[0];
                _boneMatrices[bidx++] = mat.values()[1];
                _boneMatrices[bidx++] = mat.values()[2];
                _boneMatrices[bidx++] = mat.values()[3];
                _boneMatrices[bidx++] = mat.values()[4];
                _boneMatrices[bidx++] = mat.values()[5];
            }
        }

        skinnable.invalidateDrawable();
    }

    @Override
    public void completeResolve() {
        ActorSkinnable skinnable = (ActorSkinnable) parent();
        if (skinnable == null) {
            return;
        }
        skinnable.skin(this);
        artboard.addDependency(this, (ActorComponent) skinnable);
        if (skinnable.isConnectedToBones()) {
            List<SkinnedBone> connectedBones = skinnable.connectedBones();
            for (final SkinnedBone skinnedBone : connectedBones) {
                artboard.addDependency(this, skinnedBone.node);
                List<ActorConstraint> constraints = skinnedBone.node.allConstraints();

                if (constraints != null) {
                    for (final ActorConstraint constraint : constraints) {
                        artboard.addDependency(this, constraint);
                    }
                }
            }
        }
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorSkin instance = new ActorSkin();
        instance.copyComponent(this, resetArtboard);
        return instance;
    }
}
