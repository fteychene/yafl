package org.nullpointeur.yafl.typeclass;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface Monoid<A> extends Semigroup<A> {
    A empty();

    static <T> Monoid<List<T>> list() {
        return new ListMonoid<>(Semigroup.semigroupList());
    }

    static <T> Monoid<Set<T>> set() {
        return new SetMonoid<>(Semigroup.semigroupSet());
    }

    record ListMonoid<V>(Semigroup<List<V>> semigroup) implements Monoid<List<V>> {
        @Override
        public List<V> empty() {
            return Collections.emptyList();
        }

        @Override
        public List<V> combine(List<V> a, List<V> b) {
            return semigroup.combine(a, b);
        }
    }

    record SetMonoid<V>(Semigroup<Set<V>> semigroup) implements Monoid<Set<V>> {
        @Override
        public Set<V> empty() {
            return Collections.emptySet();
        }

        @Override
        public Set<V> combine(Set<V> a, Set<V> b) {
            return semigroup.combine(a, b);
        }
    }
}
