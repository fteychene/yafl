package org.nullpointeur.yafl;

import lombok.Getter;
import org.nullpointeur.yafl.function.Function1;
import org.nullpointeur.yafl.function.Function2;
import org.nullpointeur.yafl.function.Function3;
import org.nullpointeur.yafl.function.Function4;
import org.nullpointeur.yafl.tuple.Tuple2;
import org.nullpointeur.yafl.tuple.Tuple3;
import org.nullpointeur.yafl.tuple.Tuple4;
import org.nullpointeur.yafl.tuple.Tuples;
import org.nullpointeur.yafl.typeclass.Monoid;

import java.util.List;

@Getter
public class Writer<T, Log> {
    private final T value;
    private final Log log;
    private final Monoid<Log> monoid;

    protected Writer(T value, Log log, Monoid<Log> monoid) {
        this.value = value;
        this.log = log;
        this.monoid = monoid;
    }

    public static <T, L> Writer<T, L> pure(T value, L log, Monoid<L> combine) {
        return new Writer<>(value, log, combine);
    }

    public <U> Writer<U, Log> map(Function1<T, U> f) {
        return pure(f.apply(value), log, monoid);
    }

    public <M> Writer<T, M> mapWritten(Function1<Log, M> f, Monoid<M> monoid) {
        return pure(value, f.apply(log), monoid);
    }

    public <U, M> Writer<U, M> bimap(Function1<T, U> f, Function1<Log, M> g, Monoid<M> monoid) {
        return map(f).mapWritten(g, monoid);
    }

    public <U> Writer<U, Log> flatMap(Function1<? super T, Writer<U, Log>> f) {
        Writer<U, Log> result = f.apply(value);
        return pure(result.getValue(), monoid.combine(log, result.getLog()), monoid);
    }

    public static <T, U, L> Function1<Writer<T, L>, Writer<U, L>> ap(Writer<Function1<T, U>, L> wf) {
        return w -> wf.flatMap(w::map);
    }

    public Writer<T, Log> reset() {
        return pure(value, monoid.empty(), monoid);
    }

    public Writer<T, Log> tell(Log l) {
        return pure(value, monoid.combine(log, l), monoid);
    }

    public <U> Writer<Tuple2<T, U>, Log> product(Writer<U, Log> other) {
        return mapN(other, Tuples::of);
    }

    public <U, V> Writer<Tuple3<T, U, V>, Log> product(Writer<U, Log> u, Writer<V, Log> v) {
        return mapN(u, v, Tuples::of);
    }

    public <U, V, W> Writer<Tuple4<T, U, V, W>, Log> product(Writer<U, Log> u, Writer<V, Log> v, Writer<W, Log> w) {
        return mapN(u, v, w, Tuples::of);
    }

    public <U, V> Writer<V, Log> mapN(Writer<U, Log> other, Function2<T, U, V> f) {
        return ap(other.map((U u) -> (Function1<T, V>) t -> f.apply(t, u))).apply(this);
    }

    public <U, V, W> Writer<W, Log> mapN(Writer<U, Log> u, Writer<V, Log> v, Function3<T, U, V, W> f) {
        return this.mapN(u, Tuples::of)
                .mapN(v, (tu, vv) -> f.apply(tu._1(), tu._2(), vv));
    }

    public <U, V, W, X> Writer<X, Log> mapN(Writer<U, Log> u, Writer<V, Log> v, Writer<W, Log> w, Function4<T, U, V, W, X> f) {
        return this.mapN(u, v, Tuples::of)
                .mapN(w, (tuv, ww) -> f.apply(tuv._1(), tuv._2(), tuv._3(), ww));
    }

    public static <T, L> Writer<T, List<L>> of(T value, List<L> log) {
        return pure(value, log, Monoid.list());
    }

    public <E, U> EitherWriter<E, U, Log> traverse(Function1<T, Either<E, Writer<U, Log>>> f) {
        return f.apply(getValue()).fold(
                error -> EitherWriter.pure(Either.left(error), log, monoid),
                value -> EitherWriter.pure(Either.right(value.getValue()), monoid.combine(getLog(), value.getLog()), monoid)
        );
    }

    public static <L, R, Log> Writer<Either<L, R>, Log> lift(Either<L, R> either, Monoid<Log> monoid) {
        return lift(either, monoid, l -> monoid.empty(), r -> monoid.empty());
    }

    public static <L, R, Log> Writer<Either<L, R>, Log> lift(Either<L, R> either, Monoid<Log> monoid, Function1<L, Log> logLeftFunction, Function1<R, Log> logRightFunction) {
        return either.fold(
                l -> Writer.pure(Either.left(l), logLeftFunction.apply(l), monoid),
                r -> Writer.pure(Either.right(r), logRightFunction.apply(r), monoid)
        );
    }

    public Writer<T, Log> withMonoid(Monoid<Log> newMonoid) {
        return pure(value, log, newMonoid);
    }

    public Writer<T, Log> withScopedMonoid(Monoid<Log> newMonoid, Function1<Writer<T, Log>, Writer<T, Log>> scopedFunction) {
        return scopedFunction.apply(withMonoid(newMonoid)).withMonoid(monoid);
    }

    public static void main(String[] args) {
        Writer<Integer, List<String>> writer1 = Writer.of(5, List.of("Initialized with 5"));
        Writer<Integer, List<String>> writer2 = writer1
                .map(x -> x * 2)
                .flatMap(x -> Writer.of(x + 3, List.of("Added 3")));

        System.out.println("Final Value: " + writer2.getValue()); // Final Value: 13
        System.out.println("Log: " + writer2.getLog()); // Log: Initialized with 5. Doubled the value. Added 3.

        var t = Writer.of(5, List.of("Initialized with 5"))
                .traverse(Writer::executeAction)
                .traverseEither(Writer::executeAction)
                .traverseEither(Writer::failAction)
                .flatMap(e -> Writer.lift(resetValue(10), Monoid.list(),
                        er -> List.of("Error detected " + er),
                        r -> List.of("Value reset to " + r)))
                .traverse(e -> e.flatMap(Writer::executeAction));
        System.out.println("Final Value: " + t.getValue());
        System.out.println("Log: " + t.getLog());
    }

    public static Either<String, Integer> resetValue(Integer value) {
        return Either.right(value);
    }

    public static Either<String, Writer<Integer, List<String>>> executeAction(int v) {
        return Either.right(Writer.of(v * 2, List.of("Doubled the value")));
    }

    public static Either<String, Writer<Integer, List<String>>> failAction(int v) {
        return Either.left("Error occured");
    }

    // I'm disguted by myself but it works
    public static class EitherWriter<L, R, Log> extends Writer<Either<L, R>, Log> {
        protected EitherWriter(Either<L, R> value, Log log, Monoid<Log> monoid) {
            super(null, log, monoid);
        }

        public static <L, R, Log> EitherWriter<L, R, Log> pure(Either<L, R> value, Log log, Monoid<Log> monoid) {
            return new EitherWriter<>(value, log, monoid);
        }

        public <U> EitherWriter<L, U, Log> traverseEither(Function1<R, Either<L, Writer<U, Log>>> f) {
            return traverse(v -> v.flatMap(f));
        }
    }
}