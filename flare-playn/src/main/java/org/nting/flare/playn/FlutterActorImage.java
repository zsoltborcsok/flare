package org.nting.flare.playn;

import static pythagoras.f.MathUtil.clamp;

import java.util.List;

import org.nting.flare.java.ActorArtboard;
import org.nting.flare.java.ActorImage;
import org.nting.flare.java.maths.AABB;

import playn.core.Canvas;
import playn.core.Image;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;

public class FlutterActorImage extends ActorImage implements FlutterActorDrawable {

    @Override
    public ActorArtboard artboard() {
        return artboard;
    }

    private float[] _vertexBuffer;
    private Image _image;

    public void dispose() {
        _vertexBuffer = null;
        _image = null;
    }

    public void initializeGraphics() {
        super.initializeGraphics();
        if (triangles() == null) {
            return;
        }
        _vertexBuffer = makeVertexPositionBuffer();
        float[] _uvBuffer = makeVertexUVBuffer();
        updateVertexUVBuffer(_uvBuffer);

        int count = vertexCount();
        List<Image> images = ((FlutterActor) artboard.actor()).images;
        if (textureIndex() < images.size()) {
            _image = images.get(textureIndex());
            Rectangle uvBounds = null;

            int idx = 0;
            for (int i = 0; i < count; i++) {
                _uvBuffer[idx] = _uvBuffer[idx] * _image.width();
                _uvBuffer[idx + 1] = _uvBuffer[idx + 1] * _image.height();
                if (uvBounds == null) {
                    uvBounds = new Rectangle(new Point(_uvBuffer[idx], _uvBuffer[idx + 1]));
                } else {
                    uvBounds.add(new Point(_uvBuffer[idx], _uvBuffer[idx + 1]));
                }
                idx += 2;
            }

            float[] sequenceUVs = sequenceUVs();
            if (sequenceUVs != null) {
                for (int i = 0; i < sequenceUVs.length; i++) {
                    sequenceUVs[i++] *= _image.width();
                    sequenceUVs[i] *= _image.height();
                }
            }

            if (uvBounds != null) {
                _image = _image.subImage(uvBounds.x, uvBounds.y, uvBounds.width, uvBounds.height);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (triangles() == null || renderCollapsed() || renderOpacity() <= 0 || _image == null) {
            return;
        }

        canvas.save();

        clip(canvas);

        canvas.setCompositeOperation(blendMode().getComposite());
        canvas.setAlpha(clamp(renderOpacity(), 0.0f, 1.0f));

        if (imageTransform() != null) {
            float[] m32 = imageTransform().values(); // 3x2 matrix
            canvas.transform(m32[0], m32[1], m32[2], m32[3], m32[4], m32[5]);
            canvas.drawImageCentered(_image, translation().values()[0], translation().values()[1]);
        } else {
            canvas.drawImageCentered(_image, translation().values()[0], translation().values()[1]);
        }

        canvas.restore();
    }

    @Override
    public AABB computeAABB() {
        updateVertices();

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        int readIdx = 0;
        if (_vertexBuffer != null) {
            int nv = _vertexBuffer.length / 2;

            for (int i = 0; i < nv; i++) {
                float x = _vertexBuffer[readIdx++];
                float y = _vertexBuffer[readIdx++];
                if (x < minX) {
                    minX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (y > maxY) {
                    maxY = y;
                }
            }
        }

        return new AABB(minX, minY, maxX, maxY);
    }

    @Override
    public void invalidateDrawable() {
        updateVertices();
    }

    private void updateVertices() {
        if (triangles() == null) {
            return;
        }
        updateVertexPositionBuffer(_vertexBuffer, false);
    }
}
