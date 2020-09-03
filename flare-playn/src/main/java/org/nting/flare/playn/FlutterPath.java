package org.nting.flare.playn;

import pythagoras.f.Path;

/// Abstract base path that can be invalidated and somehow
/// regenerates, no concrete logic
public interface FlutterPath {

    Path path();
}
