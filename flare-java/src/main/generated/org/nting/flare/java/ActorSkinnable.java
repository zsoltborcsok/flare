package org.nting.flare.java;

import java.util.ArrayList;
import java.util.List;

import org.nting.flare.java.maths.Mat2D;

public interface ActorSkinnable {

    void worldTransformOverride(Mat2D value);

    ActorSkin skin();

    void skin(ActorSkin skin);

    List<SkinnedBone> connectedBones();

    void connectedBones(List<SkinnedBone> connectedBones);

    default boolean isConnectedToBones() {
        return connectedBones() != null && !connectedBones().isEmpty();
    }

    public static ActorSkinnable read(ActorArtboard artboard, StreamReader reader, ActorSkinnable node) {
        reader.openArray("bones");
        int numConnectedBones = reader.readUint8Length();
        if (numConnectedBones != 0) {
            node.connectedBones(new ArrayList<SkinnedBone>(numConnectedBones));

            for (int i = 0; i < numConnectedBones; i++) {
                SkinnedBone bc = new SkinnedBone();
                reader.openObject("bone");
                bc.boneIdx = reader.readId("component");
                Mat2D.copyFromList(bc.bind, reader.readFloat32Array(6, "bind"));
                reader.closeObject();
                Mat2D.invert(bc.inverseBind, bc.bind);
                node.connectedBones().add(bc);
            }
            reader.closeArray();
            Mat2D worldOverride = new Mat2D();
            Mat2D.copyFromList(worldOverride, reader.readFloat32Array(6, "worldTransform"));
            node.worldTransformOverride(worldOverride);
        } else {
            reader.closeArray();
        }

        return node;
    }

    default void resolveSkinnable(List<ActorComponent> components) {
        if (connectedBones() != null) {
            for (SkinnedBone bc : connectedBones()) {
                bc.node = (ActorNode) components.get(bc.boneIdx);
            }
        }
    }

    default void copySkinnable(ActorSkinnable node, ActorArtboard resetArtboard) {
        if (node.connectedBones() != null) {
            connectedBones(new ArrayList<>(node.connectedBones().size()));
            for (int i = 0; i < node.connectedBones().size(); i++) {
                SkinnedBone from = node.connectedBones().get(i);
                SkinnedBone bc = new SkinnedBone();
                bc.boneIdx = from.boneIdx;
                Mat2D.copy(bc.bind, from.bind);
                Mat2D.copy(bc.inverseBind, from.inverseBind);
                connectedBones().add(bc);
            }
        }
    }

    void invalidateDrawable();
}
