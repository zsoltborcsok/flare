package org.nting.flare.java;

public class DirtyFlags {
    public static final int transformDirty = 1 << 0;
    public static final int worldTransformDirty = 1 << 1;
    public static final int paintDirty = 1 << 2;
}
