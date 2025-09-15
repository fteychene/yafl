package org.nullpointeur.yafl.tuple;

public final class Tuples {

    private Tuples() {
    }

    public static <A, B> Tuple2<A, B> of(A a, B b) {
        return new Tuple2<>(a, b);
    }

    public static <A, B, C> Tuple3<A, B, C> of(A a, B b, C c) {
        return new Tuple3<>(a, b, c);
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, B b, C c, D d) {
        return new Tuple4<>(a, b, c, d);
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(A a, B b, C c, D d, E e) {
        return new Tuple5<>(a, b, c, d, e);
    }

    public static <A, B, C> Tuple3<A, B, C> of(Tuple2<A, B> ab, C c) {
        return new Tuple3<>(ab._1(), ab._2(), c);
    }

    public static <A, B, C> Tuple3<A, B, C> of(A a, Tuple2<B, C> bc) {
        return new Tuple3<>(a, bc._1(), bc._2());
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> of(Tuple3<A, B, C> abc, D d) {
        return new Tuple4<>(abc._1(), abc._2(), abc._3(), d);
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, Tuple3<B, C, D> bcd) {
        return new Tuple4<>(a, bcd._1(), bcd._2(), bcd._3());
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> of(Tuple2<A, B> ab, Tuple2<C, D> cd) {
        return new Tuple4<>(ab._1(), ab._2(), cd._1(), cd._2());
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(Tuple4<A, B, C, D> abcd, E e) {
        return new Tuple5<>(abcd._1(), abcd._2(), abcd._3(), abcd._4(), e);
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(A a, Tuple4<B, C, D, E> bcde) {
        return new Tuple5<>(a, bcde._1(), bcde._2(), bcde._3(), bcde._4());
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(Tuple3<A, B, C> abc, Tuple2<D, E> de) {
        return new Tuple5<>(abc._1(), abc._2(), abc._3(), de._1(), de._2());
    }

    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(Tuple2<A, B> ab, Tuple3<C, D, E> cde) {
        return new Tuple5<>(ab._1(), ab._2(), cde._1(), cde._2(), cde._3());
    }

}
