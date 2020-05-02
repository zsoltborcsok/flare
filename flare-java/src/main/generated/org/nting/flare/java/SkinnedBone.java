package org.nting.flare.java;

import org.nting.flare.java.maths.Mat2D;

public class SkinnedBone {
  public int boneIdx;
  public ActorNode node;
  public Mat2D bind = new Mat2D();
  public Mat2D inverseBind = new Mat2D();
}
