package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;
import org.nting.flare.java.maths.Vec2D;

public class ActorBoneBase extends ActorNode {

    private float _length;

    public float length() {
        return _length;
    }

    public void length(float value) {
        if (_length == value) {
            return;
        }
        _length = value;
        if (children() == null) {
            return;
        }
        for (final ActorComponent component : children()) {
            if (component instanceof ActorBoneBase) {
                ((ActorBoneBase) component).x(value);
            }
        }
    }

    public Vec2D getTipWorldTranslation(Vec2D vec) {
        Mat2D transform = new Mat2D();
        transform.values()[4] = _length;
        Mat2D.multiply(transform, worldTransform(), transform);
        vec.values()[0] = transform.values()[4];
        vec.values()[1] = transform.values()[5];
        return vec;
    }

    public static ActorBoneBase read(ActorArtboard artboard, StreamReader reader, ActorBoneBase node) {
        ActorNode.read(artboard, reader, node);

        node._length = reader.readFloat32("length");

        return node;
    }

    public void copyBoneBase(ActorBoneBase node, ActorArtboard resetArtboard) {
        super.copyNode(node, resetArtboard);
        _length = node._length;
    }
}
