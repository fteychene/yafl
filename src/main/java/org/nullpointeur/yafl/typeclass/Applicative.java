package org.nullpointeur.yafl.typeclass;

import org.nullpointeur.yafl.function.Function1;
import org.nullpointeur.yafl.kind.Kind;

import java.util.function.Function;

public interface Applicative<F, A> {
    <B> Kind<F, B> pure(B value);

    <B> Function<Kind<F, A>, Kind<F, B>> ap(Kind<F, Function1<A, B>> fn);

}
