package org.nting.flare.java;

import java.util.Arrays;
import java.util.List;

import org.nting.flare.java.maths.AABB;
import org.nting.flare.java.maths.Mat2D;

public class ActorImage extends ActorDrawable implements ActorSkinnable {

    public static class SequenceFrame {
        private final int _atlasIndex;
        private final int _offset;

        public SequenceFrame(int atlasIndex, int offset) {
            _atlasIndex = atlasIndex;
            _offset = offset;
        }

        @Override
        public String toString() {
            return "(" + _atlasIndex + ", " + _offset + ")";
        }

        public int atlasIndex() {
            return _atlasIndex;
        }

        public int offset() {
            return _offset;
        }
    }

    private ActorSkin _skin;
    private List<SkinnedBone> _connectedBones;

    public int drawOrder;

    private int _textureIndex = -1;
    private float[] _vertices;
    private float[] _dynamicUV;

    public float[] dynamicUV() {
        return _dynamicUV;
    }

    private int[] _triangles;
    private int _vertexCount = 0;
    private int _triangleCount = 0;
    private float[] _animationDeformedVertices;

    private List<SequenceFrame> _sequenceFrames;
    private float[] _sequenceUVs;
    private int _sequenceFrame = 0;

    @Override
    public ActorSkin skin() {
        return _skin;
    }

    @Override
    public void skin(ActorSkin skin) {
        _skin = skin;
    }

    @Override
    public List<SkinnedBone> connectedBones() {
        return _connectedBones;
    }

    @Override
    public void connectedBones(List<SkinnedBone> connectedBones) {
        _connectedBones = connectedBones;
    }

    public int sequenceFrame() {
        return _sequenceFrame;
    }

    public float[] sequenceUVs() {
        return _sequenceUVs;
    }

    public List<SequenceFrame> sequenceFrames() {
        return _sequenceFrames;
    }

    public void sequenceFrame(int value) {
        _sequenceFrame = value;
    }

    public int textureIndex() {
        return _textureIndex;
    }

    public int vertexCount() {
        return _vertexCount;
    }

    public int triangleCount() {
        return _triangleCount;
    }

    public int[] triangles() {
        return _triangles;
    }

    public float[] vertices() {
        return _vertices;
    }

    public int vertexPositionOffset() {
        return 0;
    }

    public int vertexUVOffset() {
        return 2;
    }

    public int vertexBoneIndexOffset() {
        return 4;
    }

    public int vertexBoneWeightOffset() {
        return 8;
    }

    public int vertexStride() {
        return isConnectedToBones() ? 12 : 4;
    }

    public boolean doesAnimationVertexDeform() {
        return _animationDeformedVertices != null;
    }

    public void doesAnimationVertexDeform(boolean value) {
        if (value) {
            if (_animationDeformedVertices == null || _animationDeformedVertices.length != _vertexCount * 2) {
                _animationDeformedVertices = new float[vertexCount() * 2];
                // Copy the deform verts from the rig verts.
                int writeIdx = 0;
                int readIdx = 0;
                int readStride = vertexStride();
                for (int i = 0; i < _vertexCount; i++) {
                    _animationDeformedVertices[writeIdx++] = _vertices[readIdx];
                    _animationDeformedVertices[writeIdx++] = _vertices[readIdx + 1];
                    readIdx += readStride;
                }
            }
        } else {
            _animationDeformedVertices = null;
        }
    }

    public float[] animationDeformedVertices() {
        return _animationDeformedVertices;
    }

    public void disposeGeometry() {
        // Delete vertices only if we do not vertex deform at runtime.
        if (_animationDeformedVertices == null) {
            _vertices = null;
        }
        _triangles = null;
    }

    public static ActorImage read(ActorArtboard artboard, StreamReader reader, ActorImage node) {
        node = node != null ? node : new ActorImage();

        ActorDrawable.read(artboard, reader, node);
        ActorSkinnable.read(artboard, reader, node);

        if (!node.isHidden) {
            node._textureIndex = reader.readUint8("atlas");

            int numVertices = reader.readInt32("numVertices");

            node._vertexCount = numVertices;
            node._vertices = reader.readFloat32Array(numVertices * node.vertexStride(), "vertices");

            // In version 24 we started packing the original UV coordinates if the
            // image was marked for dynamic runtime swapping.
            if (artboard.actor().version() >= 24) {
                boolean isDynamic = reader.readBoolean("isDynamic");
                if (isDynamic) {
                    node._dynamicUV = reader.readFloat32Array(numVertices * 2, "uv");
                }
            }

            int numTris = reader.readInt32("numTriangles");
            node._triangles = new int[numTris * 3];
            node._triangleCount = numTris;
            node._triangles = reader.readUint16Array(numTris * 3, "triangles");
        }
        return node;
    }

    // TODO: fix sequences for flare.
    // static ActorImage readSequence(
    // ActorArtboard artboard, StreamReader reader, ActorImage node) {
    // ActorImage.read(artboard, reader, node);

    // if (node._textureIndex != -1) {
    // reader.openArray("frames");
    // int frameAssetCount = reader.readUint16Length();
    // // node._sequenceFrames = new ArrayList<>();
    // Float32List uvs = new Float32List(node._vertexCount * 2 * frameAssetCount);
    // int uvStride = node._vertexCount * 2;
    // node._sequenceUVs = uvs;
    // SequenceFrame firstFrame = new SequenceFrame(node._textureIndex, 0);
    // node._sequenceFrames = new ArrayList<SequenceFrame>();
    // node._sequenceFrames.add(firstFrame);
    // int readIdx = 2;
    // int writeIdx = 0;
    // int vertexStride = 4;
    // if (node._boneConnections != null && node._boneConnections.size() > 0) {
    // vertexStride = 12;
    // }
    // for (int i = 0; i < node._vertexCount; i++) {
    // uvs[writeIdx++] = node._vertices[readIdx];
    // uvs[writeIdx++] = node._vertices[readIdx + 1];
    // readIdx += vertexStride;
    // }

    // int offset = uvStride;
    // for (int i = 1; i < frameAssetCount; i++) {
    // reader.openObject("frame");

    // SequenceFrame frame =
    // SequenceFrame(reader.readUint8("atlas"), offset * 4);
    // node._sequenceFrames.add(frame);
    // reader.readFloat32ArrayOffset(uvs, uvStride, offset, "uv");
    // offset += uvStride;

    // reader.closeObject();
    // }

    // reader.closeArray();
    // }

    // return node;
    // }

    @Override
    public void resolveComponentIndices(List<ActorComponent> components) {
        super.resolveComponentIndices(components);
        resolveSkinnable(components);
    }

    @Override
    public ActorComponent makeInstance(ActorArtboard resetArtboard) {
        ActorImage instanceNode = resetArtboard.actor().makeImageNode();
        instanceNode.copyImage(this, resetArtboard);
        return instanceNode;
    }

    public void copyImage(ActorImage node, ActorArtboard resetArtboard) {
        copyDrawable(node, resetArtboard);
        copySkinnable(node, resetArtboard);

        _textureIndex = node._textureIndex;
        _vertexCount = node._vertexCount;
        _triangleCount = node._triangleCount;
        _vertices = node._vertices;
        _triangles = node._triangles;
        _dynamicUV = node._dynamicUV;
        if (node._animationDeformedVertices != null) {
            _animationDeformedVertices = Arrays.copyOf(node._animationDeformedVertices,
                    node._animationDeformedVertices.length);
        }
    }

    // void transformBind(Mat2D xform) {
    // if (_boneConnections != null) {
    // for (BoneConnection bc : _boneConnections) {
    // Mat2D.multiply(bc.bind, xform, bc.bind);
    // Mat2D.invert(bc.inverseBind, bc.bind);
    // }
    // }
    // }

    public float[] makeVertexPositionBuffer() {
        return new float[_vertexCount * 2];
    }

    public float[] makeVertexUVBuffer() {
        return new float[_vertexCount * 2];
    }

    public void transformDeformVertices(Mat2D wt) {
        if (_animationDeformedVertices == null) {
            return;
        }

        float[] fv = _animationDeformedVertices;

        int vidx = 0;
        for (int j = 0; j < _vertexCount; j++) {
            float x = fv[vidx];
            float y = fv[vidx + 1];

            fv[vidx] = wt.values()[0] * x + wt.values()[2] * y + wt.values()[4];
            fv[vidx + 1] = wt.values()[1] * x + wt.values()[3] * y + wt.values()[5];

            vidx += 2;
        }
    }

    public void updateVertexUVBuffer(float[] buffer) {
        int readIdx = vertexUVOffset();
        int writeIdx = 0;
        int stride = vertexStride();

        float[] v = _vertices;
        for (int i = 0; i < _vertexCount; i++) {
            buffer[writeIdx++] = v[readIdx];
            buffer[writeIdx++] = v[readIdx + 1];
            readIdx += stride;
        }
    }

    void updateVertexPositionBuffer(float[] buffer, boolean isSkinnedDeformInWorld) {
        Mat2D worldTransform = this.worldTransform();
        int readIdx = 0;
        int writeIdx = 0;

        float[] v = _animationDeformedVertices != null ? _animationDeformedVertices : _vertices;
        int stride = _animationDeformedVertices != null ? 2 : vertexStride();

        if (skin() != null) {
            float[] boneTransforms = skin().boneMatrices();

            // Mat2D inverseWorldTransform = Mat2D.Invert(new Mat2D(), worldTransform);
            float[] influenceMatrix = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };

            // if(this.name == "evolution_1_0001s_0003_evolution_1_weapo")
            // {
            // // print("TEST!");
            // int boneIndexOffset = vertexBoneIndexOffset;
            // int weightOffset = vertexBoneWeightOffset;
            // for(int i = 0; i < _vertexCount; i++)
            // {
            // for(int wi = 0; wi < 4; wi++)
            // {
            // int boneIndex = _vertices[boneIndexOffset+wi].toInt();
            // double weight = _vertices[weightOffset+wi];
            // if(boneIndex == 1)
            // {
            // _vertices[weightOffset+wi] = 1.0;
            // }
            // else if(boneIndex == 2)
            // {
            // _vertices[weightOffset+wi] = 0.0;
            // }
            // //print("BI $boneIndex $weight");
            // }
            // boneIndexOffset += vertexStride;
            // weightOffset += vertexStride;
            // }
            // }
            int boneIndexOffset = vertexBoneIndexOffset();
            int weightOffset = vertexBoneWeightOffset();
            for (int i = 0; i < _vertexCount; i++) {
                float x = v[readIdx];
                float y = v[readIdx + 1];

                float px, py;

                if (_animationDeformedVertices != null && isSkinnedDeformInWorld) {
                    px = x;
                    py = y;
                } else {
                    px = worldTransform.values()[0] * x + worldTransform.values()[2] * y + worldTransform.values()[4];
                    py = worldTransform.values()[1] * x + worldTransform.values()[3] * y + worldTransform.values()[5];
                }

                influenceMatrix[0] = influenceMatrix[1] = influenceMatrix[2] = influenceMatrix[3] = influenceMatrix[4] = influenceMatrix[5] = 0.0f;

                for (int wi = 0; wi < 4; wi++) {
                    int boneIndex = (int) _vertices[boneIndexOffset + wi];
                    double weight = _vertices[weightOffset + wi];

                    int boneTransformIndex = boneIndex * 6;
                    if (boneIndex <= connectedBones().size()) {
                        for (int j = 0; j < 6; j++) {
                            influenceMatrix[j] += boneTransforms[boneTransformIndex + j] * weight;
                        }
                    }
                }

                x = influenceMatrix[0] * px + influenceMatrix[2] * py + influenceMatrix[4];
                y = influenceMatrix[1] * px + influenceMatrix[3] * py + influenceMatrix[5];

                readIdx += stride;
                boneIndexOffset += vertexStride();
                weightOffset += vertexStride();

                buffer[writeIdx++] = x;
                buffer[writeIdx++] = y;
            }
        } else {
            for (int i = 0; i < _vertexCount; i++) {
                buffer[writeIdx++] = v[readIdx];
                buffer[writeIdx++] = v[readIdx + 1];
                readIdx += stride;
            }
        }
    }

    @Override
    public AABB computeAABB() {
        // Todo: implement for image.
        Mat2D worldTransform = this.worldTransform();
        return new AABB(worldTransform.values()[4], worldTransform.values()[5], worldTransform.values()[4],
                worldTransform.values()[5]);
    }

    public Mat2D imageTransform() {
        return isConnectedToBones() ? null : worldTransform();
    }

    @Override
    public void invalidateDrawable() {
    }

    @Override
    public int blendModeId() {
        return 0;
    }

    @Override
    public void blendModeId(int value) {
    }
}
