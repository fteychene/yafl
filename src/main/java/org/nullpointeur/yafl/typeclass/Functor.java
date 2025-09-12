package org.nullpointeur.yafl.typeclass;

import org.nullpointeur.yafl.kind.Kind;

public interface Functor<FK, A> {
    <B> Kind<FK, B> map(Kind<FK, A> v, java.util.function.Function<A, B> f);
}
