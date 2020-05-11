package org.nting.flare.java;

import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.TransformComponents;
import org.nting.flare.java.maths.Vec2D;

public class ActorIKConstraint extends ActorTargetedConstraint {

    public static class InfluencedBone {
        int boneIdx;
        ActorBone bone;
    }

    public static class BoneChain {
        int index;
        ActorBone bone;
        float angle;
        boolean included;
        TransformComponents transformComponents;
        Mat2D parentWorldInverse;
    }

    public static final float PI2 = (float) (Math.PI * 2.0f);
    private boolean _invertDirection = false;
    private List<InfluencedBone> _influencedBones;
    private List<BoneChain> _fkChain;
    private List<BoneChain> _boneData;

    @Override
    public void resolveComponentIndices(List<ActorComponent> components) {
        super.resolveComponentIndices(components);

        if (_influencedBones != null) {
            for (final InfluencedBone influenced : _influencedBones) {
                influenced.bone = (ActorBone) components.get(influenced.boneIdx);
                // Mark peer constraints, N.B. that we're not adding it to the
                // parent bone as we're constraining it anyway.
                if (influenced.bone != parent()) {
                    influenced.bone.addPeerConstraint(this);
                }
            }
        }
    }

    @Override
    public void completeResolve() {
        if (_influencedBones == null || _influencedBones.isEmpty()) {
            return;
        }

        // Initialize solver.
        ActorBone start = _influencedBones.get(0).bone;
        ActorNode end = _influencedBones.get(_influencedBones.size() - 1).bone;
        int count = 0;
        while (end != null && end != start.parent()) {
            count++;
            end = end.parent();
        }

        boolean allIn = count < 3;
        end = _influencedBones.get(_influencedBones.size() - 1).bone;
        _fkChain = new ArrayList<BoneChain>(count);
        int idx = count - 1;
        while (end != null && end != start.parent()) {
            BoneChain bc = new BoneChain();
            bc.bone = (ActorBone) end;
            bc.angle = 0.0f;
            bc.included = allIn;
            bc.transformComponents = new TransformComponents();
            bc.parentWorldInverse = new Mat2D();
            bc.index = idx;
            _fkChain.add(0, bc);
            end = end.parent();
        }

        // Make sure bones are good.
        _boneData = new ArrayList<BoneChain>();
        for (final InfluencedBone bone : _influencedBones) {
            BoneChain item = _fkChain.stream().filter((chainItem) -> chainItem.bone == bone.bone).findFirst()
                    .orElse(null);
            if (item == null) {
                System.out.println("Bone not in chain: " + bone.bone.name());
                continue;
            }
            _boneData.add(item);
        }
        if (!allIn) {
            // Influenced bones are in the IK chain.
            for (int i = 0; i < _boneData.size() - 1; i++) {
                BoneChain item = _boneData.get(i);
                item.included = true;
                _fkChain.get(item.index + 1).included = true;
            }
        }

        // Finally mark dependencies.
        for (final InfluencedBone bone : _influencedBones) {
            // Don't mark dependency on parent as ActorComponent already does this.
            if (bone.bone == parent()) {
                continue;
            }

            artboard.addDependency(this, bone.bone);
        }

        if (target() != null) {
            artboard.addDependency(this, target());
        }

        // All the first level children of the influenced bones should
        // depend on the final bone.
        BoneChain tip = _fkChain.get(_fkChain.size() - 1);
        for (final BoneChain fk : _fkChain) {
            if (fk == tip) {
                continue;
            }

            ActorBone bone = fk.bone;
            for (ActorComponent node : bone.children()) {
                BoneChain item = _fkChain.stream().filter((chainItem) -> chainItem.bone == node).findFirst()
                        .orElse(null);
                if (item != null) {
                    // node is in the FK chain.
                    continue;
                }
                artboard.addDependency(node, tip.bone);
            }
        }
    }

    public static ActorIKConstraint read(ActorArtboard artboard, StreamReader reader, ActorIKConstraint component) {
        component = component != null ? component : new ActorIKConstraint();
        ActorTargetedConstraint.read(artboard, reader, component);
        component._invertDirection = reader.readBoolean("isInverted");

        reader.openArray("bones");
        int numInfluencedBones = reader.readUint8Length();
        if (numInfluencedBones > 0) {
            component._influencedBones = new ArrayList<InfluencedBone>(numInfluencedBones);

            for (int i = 0; i < numInfluencedBones; i++) {
                InfluencedBone ib = new InfluencedBone();
                ib.boneIdx = reader.readId( // No label here, we're just clearing the elements from the array.
                        "");
                component._influencedBones.add(ib);
            }
        }
        reader.closeArray();
        return component;
    }

    @Override
    public void constrain(ActorNode node) {
        ActorNode target = (ActorNode) this.target();
        if (target == null) {
            return;
        }
        Vec2D worldTargetTranslation = new Vec2D();
        target.getWorldTranslation(worldTargetTranslation);

        if (_influencedBones.isEmpty()) {
            return;
        }

        // Decompose the chain.
        for (final BoneChain item : _fkChain) {
            ActorBone bone = item.bone;
            Mat2D parentWorld = bone.parent().worldTransform();
            Mat2D.invert(item.parentWorldInverse, parentWorld);
            Mat2D.multiply(bone.transform(), item.parentWorldInverse, bone.worldTransform());
            Mat2D.decompose(bone.transform(), item.transformComponents);
        }

        int count = _boneData.size();
        if (count == 1) {
            solve1(_boneData.get(0), worldTargetTranslation);
        } else if (count == 2) {
            solve2(_boneData.get(0), _boneData.get(1), worldTargetTranslation);
        } else {
            BoneChain tip = _boneData.get(count - 1);
            for (int i = 0; i < count - 1; i++) {
                BoneChain item = _boneData.get(i);
                solve2(item, tip, worldTargetTranslation);
                for (int j = item.index + 1; j < _fkChain.size() - 1; j++) {
                    BoneChain fk = _fkChain.get(j);
                    Mat2D.invert(fk.parentWorldInverse, fk.bone.parent().worldTransform());
                }
            }
        }

        // At the end, mix the FK angle with the IK angle by strength
        if (strength() != 1.0f) {
            for (final BoneChain fk : _fkChain) {
                if (!fk.included) {
                    ActorBone bone = fk.bone;
                    Mat2D.multiply(bone.worldTransform(), bone.parent().worldTransform(), bone.transform());
                    continue;
                }
                float fromAngle = fk.transformComponents.rotation() % PI2;
                float toAngle = fk.angle % PI2;
                float diff = toAngle - fromAngle;
                if (diff > Math.PI) {
                    diff -= PI2;
                } else if (diff < -Math.PI) {
                    diff += PI2;
                }
                float angle = fromAngle + diff * strength();
                constrainRotation(fk, angle);
            }
        }
    }

    public void constrainRotation(BoneChain fk, float rotation) {
        ActorBone bone = fk.bone;
        Mat2D parentWorld = bone.parent().worldTransform();
        Mat2D transform = bone.transform();
        TransformComponents c = fk.transformComponents;

        if (rotation == 0.0f) {
            Mat2D.identity(transform);
        } else {
            Mat2D.fromRotation(transform, rotation);
        }
        // Translate
        transform.values()[4] = c.x();
        transform.values()[5] = c.y();
        // Scale
        float scaleX = c.scaleX();
        float scaleY = c.scaleY();
        transform.values()[0] *= scaleX;
        transform.values()[1] *= scaleX;
        transform.values()[2] *= scaleY;
        transform.values()[3] *= scaleY;
        // Skew
        float skew = c.skew();
        if (skew != 0.0f) {
            transform.values()[2] = transform.values()[0] * skew + transform.values()[2];
            transform.values()[3] = transform.values()[1] * skew + transform.values()[3];
        }

        Mat2D.multiply(bone.worldTransform(), parentWorld, transform);
    }

    public void solve1(BoneChain fk1, Vec2D worldTargetTranslation) {
        Mat2D iworld = fk1.parentWorldInverse;
        Vec2D pA = new Vec2D();
        fk1.bone.getWorldTranslation(pA);
        Vec2D pBT = new Vec2D(worldTargetTranslation);

        // To target in worldspace
        Vec2D toTarget = Vec2D.subtract(new Vec2D(), pBT, pA);
        // Note this is directional, hence not transformMat2d
        Vec2D toTargetLocal = Vec2D.transformMat2(new Vec2D(), toTarget, iworld);
        float r = (float) atan2(toTargetLocal.values()[1], toTargetLocal.values()[0]);

        constrainRotation(fk1, r);
        fk1.angle = r;
    }

    public void solve2(BoneChain fk1, BoneChain fk2, Vec2D worldTargetTranslation) {
        ActorBone b1 = fk1.bone;
        ActorBone b2 = fk2.bone;
        BoneChain firstChild = _fkChain.get(fk1.index + 1);

        Mat2D iworld = fk1.parentWorldInverse;

        Vec2D pA = b1.getWorldTranslation(new Vec2D());
        Vec2D pC = firstChild.bone.getWorldTranslation(new Vec2D());
        Vec2D pB = b2.getTipWorldTranslation(new Vec2D());

        Vec2D pBT = new Vec2D(worldTargetTranslation);

        pA = Vec2D.transformMat2D(pA, pA, iworld);
        pC = Vec2D.transformMat2D(pC, pC, iworld);
        pB = Vec2D.transformMat2D(pB, pB, iworld);
        pBT = Vec2D.transformMat2D(pBT, pBT, iworld);

        // http://mathworld.wolfram.com/LawofCosines.html
        Vec2D av = Vec2D.subtract(new Vec2D(), pB, pC);
        float a = Vec2D.length(av);

        Vec2D bv = Vec2D.subtract(new Vec2D(), pC, pA);
        float b = Vec2D.length(bv);

        Vec2D cv = Vec2D.subtract(new Vec2D(), pBT, pA);
        float c = Vec2D.length(cv);

        float A = (float) acos(max(-1, min(1, (-a * a + b * b + c * c) / (2 * b * c))));
        float C = (float) acos(max(-1, min(1, (a * a + b * b - c * c) / (2 * a * b))));

        float r1, r2;
        if (b2.parent() != b1) {
            BoneChain secondChild = _fkChain.get(fk1.index + 2);

            Mat2D secondChildWorldInverse = secondChild.parentWorldInverse;

            pC = firstChild.bone.getWorldTranslation(new Vec2D());
            pB = b2.getTipWorldTranslation(new Vec2D());

            Vec2D avec = Vec2D.subtract(new Vec2D(), pB, pC);
            Vec2D avLocal = Vec2D.transformMat2(new Vec2D(), avec, secondChildWorldInverse);
            float angleCorrection = (float) -atan2(avLocal.values()[1], avLocal.values()[0]);

            if (_invertDirection) {
                r1 = (float) (atan2(cv.values()[1], cv.values()[0]) - A);
                r2 = (float) (-C + Math.PI + angleCorrection);
            } else {
                r1 = (float) (A + atan2(cv.values()[1], cv.values()[0]));
                r2 = (float) (C - Math.PI + angleCorrection);
            }
        } else if (_invertDirection) {
            r1 = (float) (atan2(cv.values()[1], cv.values()[0]) - A);
            r2 = (float) (-C + Math.PI);
        } else {
            r1 = (float) (A + atan2(cv.values()[1], cv.values()[0]));
            r2 = (float) (C - Math.PI);
        }

        constrainRotation(fk1, r1);
        constrainRotation(firstChild, r2);
        if (firstChild != fk2) {
            ActorBone bone = fk2.bone;
            Mat2D.multiply(bone.worldTransform(), bone.parent().worldTransform(), bone.transform());
        }

        // Simple storage, need this for interpolation.
        fk1.angle = r1;
        firstChild.angle = r2;
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard artboard) {
        ActorIKConstraint instance = new ActorIKConstraint();
        instance.copyIKConstraint(this, artboard);
        return instance;
    }

    public void copyIKConstraint(ActorIKConstraint node, ActorArtboard artboard) {
        copyTargetedConstraint(node, artboard);

        _invertDirection = node._invertDirection;
        if (node._influencedBones != null) {
            _influencedBones = new ArrayList<InfluencedBone>(node._influencedBones.size());
            for (int i = 0; i < node._influencedBones.size(); i++) {
                InfluencedBone ib = new InfluencedBone();
                ib.boneIdx = node._influencedBones.get(i).boneIdx;
                _influencedBones.add(ib);
            }
        }
    }

    @Override
    public void update(int dirt) {
    }
}
