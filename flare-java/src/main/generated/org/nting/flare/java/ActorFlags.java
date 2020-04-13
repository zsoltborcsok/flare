package org.nting.flare.java;

public class ActorFlags {
  static const int isDrawOrderDirty = 1 << 0;
  static const int isDirty = 1 << 1;
}

public class DirtyFlags {
  static const int transformDirty = 1 << 0;
  static const int worldTransformDirty = 1 << 1;
  static const int paintDirty = 1 << 2;
}
