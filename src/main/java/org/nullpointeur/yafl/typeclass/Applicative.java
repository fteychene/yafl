package org.nullpointeur.yafl.typeclass;

import org.nullpointeur.yafl.kind.Kind;

import java.util.function.Function;

public interface Applicative<F, A> {
    <B> Kind<F, B> pure(B value);

    <B> Function<Kind<F, A>, Kind<F, B>> ap(Kind<F, Function<A, B>> fn);

    default <B> Kind<F, B> ap(Kind<F, Function<A, B>> fn, Kind<F, A> v) {
        return ap(fn).apply(v);
    }
}
