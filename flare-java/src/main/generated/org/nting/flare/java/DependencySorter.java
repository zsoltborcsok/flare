package org.nting.flare.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencySorter {

    private final Set<ActorComponent> _perm;
    private final Set<ActorComponent> _temp;
    private List<ActorComponent> _order;

    public DependencySorter() {
        _perm = new HashSet<>();
        _temp = new HashSet<>();
    }

    public List<ActorComponent> sort(ActorComponent root) {
        _order = new ArrayList<>();
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
            System.out.println("Dependency cycle!");
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
        _order.add(0, n);

        return true;
    }
}
