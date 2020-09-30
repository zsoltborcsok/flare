package org.nting.flare.playn.util;

import java.util.List;

import playn.core.PathContour;
import pythagoras.f.Path;

public class TrimPathUtil {

    private static float _appendPathSegmentSequential(List<PathContour> contours, Path to, float offset, float start,
            float stop) {
        float nextOffset = offset;
        for (PathContour contour : contours) {
            nextOffset += offset + contour.length;
            if (start < nextOffset) {
                Path extracted = contour.extractPath((start - offset) / contour.length,
                        (stop - offset) / contour.length);
                if (extracted != null && !extracted.isEmpty()) {
                    to.append(extracted, false);
                }
                if (stop < nextOffset) {
                    break;
                }
            }
            offset = nextOffset;
        }
        return offset;
    }

    private static void _appendPathSegmentSync(PathContour contour, Path to, float offset, float start, float stop) {
        float nextOffset = offset + contour.length;
        if (start < nextOffset) {
            Path extracted = contour.extractPath((start - offset) / contour.length, (stop - offset) / contour.length);
            if (extracted != null) {
                to.append(extracted, false);
            }
        }
    }

    private static Path _trimPathSequential(List<PathContour> contours, float startT, float stopT, boolean complement) {
        final Path result = new Path();

        float totalLength = contours.stream().map(c -> c.length).reduce(0f, Float::sum);

        // Reset metrics from the start.
        float trimStart = totalLength * startT;
        float trimStop = totalLength * stopT;

        if (complement) {
            if (trimStart > 0.0f) {
                _appendPathSegmentSequential(contours, result, 0.0f, 0.0f, trimStart);
            }
            if (trimStop < totalLength) {
                _appendPathSegmentSequential(contours, result, 0.0f, trimStop, totalLength);
            }
        } else {
            if (trimStart < trimStop) {
                _appendPathSegmentSequential(contours, result, 0.0f, trimStart, trimStop);
            }
        }

        return result;
    }

    private static Path _trimPathSync(List<PathContour> contours, float startT, float stopT, boolean complement) {
        final Path result = new Path();

        for (PathContour contour : contours) {
            float length = contour.length;
            float trimStart = length * startT;
            float trimStop = length * stopT;

            if (complement) {
                if (trimStart > 0.0) {
                    _appendPathSegmentSync(contour, result, 0.0f, 0.0f, trimStart);
                }
                if (trimStop < length) {
                    _appendPathSegmentSync(contour, result, 0.0f, trimStop, length);
                }
            } else {
                if (trimStart < trimStop) {
                    _appendPathSegmentSync(contour, result, 0.0f, trimStart, trimStop);
                }
            }
        }
        return result;
    }

    public static Path trimPath(playn.core.Path path, float startT, float stopT, boolean complement,
            boolean isSequential) {
        List<PathContour> contours = path.contours();
        if (isSequential) {
            return _trimPathSequential(contours, startT, stopT, complement);
        } else {
            return _trimPathSync(contours, startT, stopT, complement);
        }
    }
}