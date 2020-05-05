package org.nting.flare.java;

import java.util.ArrayList;
import java.util.List;

public class DependencySorter {
  private HashSet<ActorComponent> _perm;
  private HashSet<ActorComponent> _temp;
  private List<ActorComponent> _order;

  DependencySorter() {
    _perm = HashSet<ActorComponent>();
    _temp = HashSet<ActorComponent>();
  }

  public List<ActorComponent> sort(ActorComponent root) {
    _order = new ArrayList<ActorComponent>();
    if (!visit(root)) {
      return null;
    }
    return _order;
  }

  public boolean visit(ActorComponent n) {
    if (_perm.contains(n)) {
      return true;
    }
    if (_temp.contains(n)) {
      print("Dependency cycle!");
      return false;
    }

    _temp.add(n);

    List<ActorComponent> dependents = n.dependents;
    if (dependents != null) {
      for (final ActorComponent d : dependents) {
        if (!visit(d)) {
          return false;
        }
      }
    }
    _perm.add(n);
    _order.insert(0, n);

    return true;
  }
}
