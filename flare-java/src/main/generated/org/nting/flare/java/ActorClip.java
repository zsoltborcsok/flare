package org.nting.flare.java;

public class ActorClip {
  int clipIdx;
  boolean intersect = true;
  ActorNode node;

  ActorClip(this.clipIdx);

  ActorClip.copy(ActorClip from)
      : clipIdx = from.clipIdx,
        intersect = from.intersect;
}
