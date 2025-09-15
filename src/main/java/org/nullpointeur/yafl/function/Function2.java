package org.nullpointeur.yafl.function;

import java.io.Serializable;

@FunctionalInterface
public interface Function2<T1, T2, R> extends Serializable, java.util.function.BiFunction<T1, T2, R> {


    default Function1<T2, R> partial1(T1 t1) {
        return t2 -> apply(t1, t2);
    }

    default Function1<T1, R> partial2(T2 t2) {
        return t1 -> apply(t1, t2);
    }
}
