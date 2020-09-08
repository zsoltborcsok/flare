package org.nting.flare.java;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

import com.google.common.collect.Sets;

public class JellyComponent extends ActorComponent {

    public static final int jellyMax = 16;
    public static float optimalDistance = (float) (4.0 * (sqrt(2.0) - 1.0) / 3.0);
    public static float curveConstant = (float) (optimalDistance * sqrt(2.0) * 0.5);
    public static final float epsilon = 0.001f; // Intentionally agressive.

    public static boolean fuzzyEquals(Vec2D a, Vec2D b) {
        float a0 = a.values()[0], a1 = a.values()[1];
        float b0 = b.values()[0], b1 = b.values()[1];
        return abs(a0 - b0) <= epsilon * max(1.0f, max(abs(a0), abs(b0)))
                && abs(a1 - b1) <= epsilon * max(1.0, max(abs(a1), abs(b1)));
    }

    public static void forwardDiffBezier(float c0, float c1, float c2, float c3, List<Vec2D> points, int count,
            int offset) {
        float f = count;

        float p0 = c0;

        float p1 = 3.0f * (c1 - c0) / f;

        f *= count;
        float p2 = 3.0f * (c0 - 2.0f * c1 + c2) / f;

        f *= count;
        float p3 = (c3 - c0 + 3.0f * (c1 - c2)) / f;

        c0 = p0;
        c1 = p1 + p2 + p3;
        c2 = 2 * p2 + 6 * p3;
        c3 = 6 * p3;

        for (int a = 0; a <= count; a++) {
            points.get(a).values()[offset] = c0;
            c0 += c1;
            c1 += c2;
            c2 += c3;
        }
    }

    public List<Vec2D> normalizeCurve(List<Vec2D> curve, int numSegments) {
        List<Vec2D> points = new ArrayList<>();
        int curvePointCount = curve.size();
        List<Float> distances = new ArrayList<>(curvePointCount);
        distances.add(0.0f);
        for (int i = 0; i < curvePointCount - 1; i++) {
            Vec2D p1 = curve.get(i);
            Vec2D p2 = curve.get(i + 1);
            distances.add(i + 1, distances.get(i) + Vec2D.distance(p1, p2));
        }
        float totalDistance = distances.get(curvePointCount - 1);

        float segmentLength = totalDistance / numSegments;
        int pointIndex = 1;
        for (int i = 1; i <= numSegments; i++) {
            float distance = segmentLength * i;

            while (pointIndex < curvePointCount - 1 && distances.get(pointIndex) < distance) {
                pointIndex++;
            }

            float d = distances.get(pointIndex);
            float lastCurveSegmentLength = d - distances.get(pointIndex - 1);
            float remainderOfDesired = d - distance;
            float ratio = remainderOfDesired / lastCurveSegmentLength;
            float iratio = 1.0f - ratio;

            Vec2D p1 = curve.get(pointIndex - 1);
            Vec2D p2 = curve.get(pointIndex);
            points.add(new Vec2D(p1.values()[0] * ratio + p2.values()[0] * iratio,
                    p1.values()[1] * ratio + p2.values()[1] * iratio));
        }

        return points;
    }

    private float _easeIn;
    private float _easeOut;
    private float _scaleIn;
    private float _scaleOut;
    private int _inTargetIdx;
    private int _outTargetIdx;
    private ActorNode _inTarget;
    private ActorNode _outTarget;
    private List<ActorJellyBone> _bones;
    private Vec2D _inPoint;
    private Vec2D _inDirection;
    private Vec2D _outPoint;
    private Vec2D _outDirection;

    private Vec2D _cachedTip;
    private Vec2D _cachedOut;
    private Vec2D _cachedIn;
    private float _cachedScaleIn;
    private float _cachedScaleOut;

    private List<Vec2D> _jellyPoints;

    public ActorNode inTarget() {
        return _inTarget;
    }

    public ActorNode outTarget() {
        return _inTarget;
    }

    public JellyComponent() {
        _inPoint = new Vec2D();
        _inDirection = new Vec2D();
        _outPoint = new Vec2D();
        _outDirection = new Vec2D();
        _cachedTip = new Vec2D();
        _cachedOut = new Vec2D();
        _cachedIn = new Vec2D();

        _jellyPoints = new ArrayList<>(jellyMax + 1);
        for (int i = 0; i <= jellyMax; i++) {
            _jellyPoints.add(new Vec2D());
        }
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard artboard) {
        JellyComponent instance = new JellyComponent();
        instance.copyJelly(this, artboard);
        return instance;
    }

    public void copyJelly(JellyComponent component, ActorArtboard artboard) {
        super.copyComponent(component, artboard);
        _easeIn = component._easeIn;
        _easeOut = component._easeOut;
        _scaleIn = component._scaleIn;
        _scaleOut = component._scaleOut;
        _inTargetIdx = component._inTargetIdx;
        _outTargetIdx = component._outTargetIdx;
    }

    @Override
    public void resolveComponentIndices(List<ActorComponent> components) {
        super.resolveComponentIndices(components);

        if (_inTargetIdx != 0) {
            _inTarget = (ActorNode) components.get(_inTargetIdx);
        }
        if (_outTargetIdx != 0) {
            _outTarget = (ActorNode) components.get(_outTargetIdx);
        }

        List<ActorConstraint> dependencyConstraints = new ArrayList<>();
        ActorBone bone = (ActorBone) parent();
        if (bone != null) {
            artboard.addDependency(this, bone);
            dependencyConstraints.addAll(bone.allConstraints());
            ActorBone firstBone = bone.firstBone();
            if (firstBone != null) {
                artboard.addDependency(this, firstBone);
                dependencyConstraints.addAll(firstBone.allConstraints());

                // If we don't have an out target and the child jelly does have an
                // in target we are dependent on that target's position.
                if (_outTarget == null && firstBone.jelly != null && firstBone.jelly.inTarget() != null) {
                    artboard.addDependency(this, firstBone.jelly.inTarget());
                    dependencyConstraints.addAll(firstBone.jelly.inTarget().allConstraints());
                }
            }
            if (bone.parent() instanceof ActorBone) {
                ActorBone parentBone = (ActorBone) bone.parent();
                JellyComponent parentBoneJelly = parentBone.jelly;
                if (parentBoneJelly != null && parentBoneJelly.outTarget() != null) {
                    artboard.addDependency(this, parentBoneJelly.outTarget());
                    dependencyConstraints.addAll(parentBoneJelly.outTarget().allConstraints());
                }
            }
        }

        if (_inTarget != null) {
            artboard.addDependency(this, _inTarget);
            dependencyConstraints.addAll(_inTarget.allConstraints());
        }
        if (_outTarget != null) {
            artboard.addDependency(this, _outTarget);
            dependencyConstraints.addAll(_outTarget.allConstraints());
        }

        // We want to depend on any and all constraints that our dependents have.
        Set<ActorConstraint> constraints = Sets.newHashSet(dependencyConstraints);
        for (final ActorConstraint constraint : constraints) {
            artboard.addDependency(this, constraint);
        }
    }

    @Override
    public void completeResolve() {
        // super.completeResolve();
        ActorBone bone = (ActorBone) parent();
        bone.jelly = this;

        // Get jellies.
        List<ActorComponent> children = bone.children();
        if (children == null) {
            return;
        }

        _bones = new ArrayList<>();
        for (final ActorComponent child : children) {
            if (child instanceof ActorJellyBone) {
                _bones.add((ActorJellyBone) child);
                // Make sure the jelly doesn't update until
                // the jelly component has updated
                artboard.addDependency(child, this);
            }
        }
    }

    public static JellyComponent read(ActorArtboard artboard, StreamReader reader, JellyComponent node) {
        node = node != null ? node : new JellyComponent();
        ActorComponent.read(artboard, reader, node);

        node._easeIn = reader.readFloat32("easeIn");
        node._easeOut = reader.readFloat32("easeOut");
        node._scaleIn = reader.readFloat32("scaleIn");
        node._scaleOut = reader.readFloat32("scaleOut");
        node._inTargetIdx = reader.readId("inTargetId");
        node._outTargetIdx = reader.readId("outTargetId");

        return node;
    }

    public void updateJellies() {
        if (_bones == null) {
            return;
        }
        ActorBone bone = (ActorBone) parent();
        // We are in local bone space.
        Vec2D tipPosition = new Vec2D(bone.length(), 0.0f);

        if (fuzzyEquals(_cachedTip, tipPosition) && fuzzyEquals(_cachedOut, _outPoint)
                && fuzzyEquals(_cachedIn, _inPoint) && _cachedScaleIn == _scaleIn && _cachedScaleOut == _scaleOut) {
            return;
        }

        Vec2D.copy(_cachedTip, tipPosition);
        Vec2D.copy(_cachedOut, _outPoint);
        Vec2D.copy(_cachedIn, _inPoint);
        _cachedScaleIn = _scaleIn;
        _cachedScaleOut = _scaleOut;

        Vec2D q0 = new Vec2D();
        Vec2D q1 = _inPoint;
        Vec2D q2 = _outPoint;
        Vec2D q3 = tipPosition;

        forwardDiffBezier(q0.values()[0], q1.values()[0], q2.values()[0], q3.values()[0], _jellyPoints, jellyMax, 0);
        forwardDiffBezier(q0.values()[1], q1.values()[1], q2.values()[1], q3.values()[1], _jellyPoints, jellyMax, 1);

        List<Vec2D> normalizedPoints = normalizeCurve(_jellyPoints, _bones.size());

        Vec2D lastPoint = _jellyPoints.get(0);

        float scale = _scaleIn;
        float scaleInc = (_scaleOut - _scaleIn) / (_bones.size() - 1);
        for (int i = 0; i < normalizedPoints.size(); i++) {
            ActorJellyBone jelly = _bones.get(i);
            Vec2D p = normalizedPoints.get(i);

            jelly.translation(lastPoint);
            jelly.length(Vec2D.distance(p, lastPoint));
            jelly.scaleY(scale);
            scale += scaleInc;

            Vec2D diff = Vec2D.subtract(new Vec2D(), p, lastPoint);
            jelly.rotation((float) atan2(diff.values()[1], diff.values()[0]));
            lastPoint = p;
        }
    }

    @Override
    public void onDirty(int dirt) {
        // Intentionally empty. Doesn't throw dirt around.
    }

    @Override
    public void update(int dirt) {
        ActorBone bone = (ActorBone) parent();
        ActorNode parentBone = bone.parent();
        JellyComponent parentBoneJelly = null;
        if (parentBone instanceof ActorBone) {
            parentBoneJelly = ((ActorBone) parentBone).jelly;
        }

        Mat2D inverseWorld = new Mat2D();
        if (!Mat2D.invert(inverseWorld, bone.worldTransform())) {
            return;
        }

        if (_inTarget != null) {
            Vec2D translation = _inTarget.getWorldTranslation(new Vec2D());
            Vec2D.transformMat2D(_inPoint, translation, inverseWorld);
            Vec2D.normalize(_inDirection, _inPoint);
        } else if (parentBone != null) {
            ActorBone firstBone = null;
            if (parentBone instanceof ActorBone) {
                firstBone = ((ActorBone) parentBone).firstBone();
            } else if (parentBone instanceof ActorRootBone) {
                firstBone = ((ActorRootBone) parentBone).firstBone();
            }
            if (firstBone == bone && parentBoneJelly != null && parentBoneJelly._outTarget != null) {
                Vec2D translation = parentBoneJelly._outTarget.getWorldTranslation(new Vec2D());
                Vec2D localParentOut = Vec2D.transformMat2D(new Vec2D(), translation, inverseWorld);
                Vec2D.normalize(localParentOut, localParentOut);
                Vec2D.negate(_inDirection, localParentOut);
            } else {
                Vec2D d1 = new Vec2D(1.0f, 0.0f);
                Vec2D d2 = new Vec2D(1.0f, 0.0f);

                Vec2D.transformMat2(d1, d1, parentBone.worldTransform());
                Vec2D.transformMat2(d2, d2, bone.worldTransform());

                Vec2D sum = Vec2D.add(new Vec2D(), d1, d2);
                Vec2D.transformMat2(_inDirection, sum, inverseWorld);
                Vec2D.normalize(_inDirection, _inDirection);
            }
            _inPoint.values()[0] = _inDirection.values()[0] * _easeIn * bone.length() * curveConstant;
            _inPoint.values()[1] = _inDirection.values()[1] * _easeIn * bone.length() * curveConstant;
        } else {
            _inDirection.values()[0] = 1.0f;
            _inDirection.values()[1] = 0.0f;
            _inPoint.values()[0] = _inDirection.values()[0] * _easeIn * bone.length() * curveConstant;
        }

        if (_outTarget != null) {
            Vec2D translation = _outTarget.getWorldTranslation(new Vec2D());
            Vec2D.transformMat2D(_outPoint, translation, inverseWorld);
            Vec2D tip = new Vec2D(bone.length(), 0.0f);
            Vec2D.subtract(_outDirection, _outPoint, tip);
            Vec2D.normalize(_outDirection, _outDirection);
        } else if (bone.firstBone() != null) {
            ActorBone firstBone = bone.firstBone();
            JellyComponent firstBoneJelly = firstBone.jelly;
            if (firstBoneJelly != null && firstBoneJelly._inTarget != null) {
                Vec2D translation = firstBoneJelly._inTarget.getWorldTranslation(new Vec2D());
                Vec2D worldChildInDir = Vec2D.subtract(new Vec2D(), firstBone.getWorldTranslation(new Vec2D()),
                        translation);
                Vec2D.transformMat2(_outDirection, worldChildInDir, inverseWorld);
            } else {
                Vec2D d1 = new Vec2D(1.0f, 0.0f);
                Vec2D d2 = new Vec2D(1.0f, 0.0f);

                Vec2D.transformMat2(d1, d1, firstBone.worldTransform());
                Vec2D.transformMat2(d2, d2, bone.worldTransform());

                Vec2D sum = Vec2D.add(new Vec2D(), d1, d2);
                Vec2D negativeSum = Vec2D.negate(new Vec2D(), sum);
                Vec2D.transformMat2(_outDirection, negativeSum, inverseWorld);
                Vec2D.normalize(_outDirection, _outDirection);
            }
            Vec2D.normalize(_outDirection, _outDirection);
            Vec2D scaledOut = Vec2D.scale(new Vec2D(), _outDirection, _easeOut * bone.length() * curveConstant);
            _outPoint.values()[0] = bone.length();
            _outPoint.values()[1] = 0.0f;
            Vec2D.add(_outPoint, _outPoint, scaledOut);
        } else {
            _outDirection.values()[0] = -1.0f;
            _outDirection.values()[1] = 0.0f;

            Vec2D scaledOut = Vec2D.scale(new Vec2D(), _outDirection, _easeOut * bone.length() * curveConstant);
            _outPoint.values()[0] = bone.length();
            _outPoint.values()[1] = 0.0f;
            Vec2D.add(_outPoint, _outPoint, scaledOut);
        }

        updateJellies();
    }
}
