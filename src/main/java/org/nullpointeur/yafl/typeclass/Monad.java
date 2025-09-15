package org.nullpointeur.yafl.typeclass;

import org.nullpointeur.yafl.kind.Kind;

public interface Monad<F, A> {

    <B> Kind<F, B> pure(B value);

    <B> Kind<F, B> flatMap(Kind<F, A> v, java.util.function.Function<A, Kind<F, B>> f);
}
