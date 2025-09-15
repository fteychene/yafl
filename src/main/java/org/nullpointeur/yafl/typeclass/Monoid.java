package org.nullpointeur.yafl.typeclass;

public interface Monoid<A> extends Semigroup<A> {
    A empty();
}
