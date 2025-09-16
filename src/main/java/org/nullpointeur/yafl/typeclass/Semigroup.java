package org.nullpointeur.yafl.typeclass;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public interface Semigroup<A> {

    A combine(A a1, A a2);

    static <T> Semigroup<List<T>> semigroupList() {
        return new ListSemigroup<>();
    }

    static <T> Semigroup<Set<T>> semigroupSet() {
        return new SetSemigroup<>();
    }

    class ListSemigroup<A> implements Semigroup<List<A>> {
        @Override
        public List<A> combine(List<A> a1, List<A> a2) {
            return Stream.concat(a1.stream(), a2.stream()).toList();
        }
    }

    class SetSemigroup<A> implements Semigroup<Set<A>> {
        @Override
        public Set<A> combine(Set<A> a1, Set<A> a2) {
            return Stream.concat(a1.stream(), a2.stream()).collect(toSet());
        }
    }
}
