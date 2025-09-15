package org.nullpointeur.yafl.function;

import java.io.Serializable;

@FunctionalInterface
public interface Function3<T1, T2, T3, R> extends Serializable {

    R apply(T1 t1, T2 t2, T3 t3);

    default Function2<T2, T3, R> partial1(T1 t1) {
        return (t2, t3) -> apply(t1, t2, t3);
    }

    default Function2<T1, T3, R> partial2(T2 t2) {
        return (t1, t3) -> apply(t1, t2, t3);
    }

    default Function2<T1, T2, R> partial3(T3 t3) {
        return (t1, t2) -> apply(t1, t2, t3);
    }

}
