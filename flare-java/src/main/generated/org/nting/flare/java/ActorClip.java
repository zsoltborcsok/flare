package org.nting.flare.java;

public class ActorClip {
  public int clipIdx;
  public boolean intersect = true;
  public ActorNode node;

  ActorClip(this.clipIdx);

  ActorClip.copy(ActorClip from)
      : clipIdx = from.clipIdx,
        intersect = from.intersect;
}
