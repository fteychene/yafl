package org.nullpointeur.yafl;

import org.nullpointeur.yafl.function.Function1;
import org.nullpointeur.yafl.function.Function2;
import org.nullpointeur.yafl.function.Function3;
import org.nullpointeur.yafl.function.Function4;
import org.nullpointeur.yafl.kind.Kind;
import org.nullpointeur.yafl.tuple.Tuple2;
import org.nullpointeur.yafl.tuple.Tuple3;
import org.nullpointeur.yafl.tuple.Tuple4;
import org.nullpointeur.yafl.tuple.Tuples;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Option<A> extends Kind<Option.ForOption, A> {

    record Some<A>(A value) implements Option<A> {
    }

    record None<A>() implements Option<A> {
    }

    static <A> Option<A> some(A value) {
        return new Some<>(value);
    }

    static <A> Option<A> none() {
        return new None<>();
    }

    default <B> Option<B> map(Function1<A, B> f) {
        return (Option<B>) instanceFunctor().map(this, f);
    }

    default <B> Option<B> apply(Option<Function1<A, B>> f) {
        return (Option<B>) instanceApplicative().ap(f).apply(this);
    }

    default <B> Option<B> flatMap(Function1<A, Option<B>> f) {
        return (Option<B>) instanceMonad().flatMap(this, f.andThen(kind -> (Option<B>) kind));
    }

    static <A, B> Function1<Option<A>, Option<B>> ap(Option<Function1<A, B>> f) {
        return a -> a.apply(f);
    }

    default <B, C> Option<C> mapN(Option<B> ob, Function2<A, B, C> f) {
        return (Option<C>) instanceApplicative().ap(ob.map(b -> (Function1<A, C>) a -> f.apply(a, b))).apply(this);
    }

    default <B, C, D> Option<D> mapN(Option<B> ob, Option<C> oc, Function3<A, B, C, D> f) {
        return this.mapN(ob, (a, b) -> (Function1<C, D>) c -> f.apply(a, b, c))
                .mapN(oc, Function::apply);
    }

    default <B, C, D, E> Option<E> mapN(Option<B> ob, Option<C> oc, Option<D> od, Function4<A, B, C, D, E> f) {
        return this.mapN(ob, oc, (a, b, c) -> (Function1<D, E>) d -> f.apply(a, b, c, d))
                .mapN(od, Function::apply);
    }

    default <B> Option<Tuple2<A, B>> zip(Option<B> ob) {
        return mapN(ob, Tuples::of);
    }

    default <B, C> Option<Tuple3<A, B, C>> zip(Option<B> ob, Option<C> oc) {
        return mapN(ob, oc, Tuples::of);
    }

    default <B, C, D> Option<Tuple4<A, B, C, D>> zip(Option<B> ob, Option<C> oc, Option<D> od) {
        return mapN(ob, oc, od, Tuples::of);
    }

    default A getOrElse(Supplier<A> other) {
        return switch (this) {
            case Some<A> some -> some.value();
            case None<A> ignored -> other.get();
        };
    }

    default A getOrElse(A other) {
        return getOrElse(() -> other);
    }

    default void peek(Consumer<A> consumer) {
        if (this instanceof Some<A>(A value)) {
            consumer.accept(value);
        }
    }

    default <B> B fold(B ifNone, Function<A, B> ifSome) {
        return switch (this) {
            case Some<A> some -> ifSome.apply(some.value());
            case None<A> ignored -> ifNone;
        };
    }

    default <L> Either<L, A> toEither(Supplier<L> left) {
        return fold(Either.left(left.get()), Either::right);
    }

    // Test plumbing for type classes

    static <A> org.nullpointeur.yafl.typeclass.Functor<ForOption, A> functor() {
        return new Functor<>();
    }

    default org.nullpointeur.yafl.typeclass.Functor<ForOption, A> instanceFunctor() {
        return functor();
    }

    static <A> org.nullpointeur.yafl.typeclass.Applicative<ForOption, A> applicative() {
        return new Applicative<>(functor());
    }

    default org.nullpointeur.yafl.typeclass.Applicative<ForOption, A> instanceApplicative() {
        return applicative();
    }

    static <A> org.nullpointeur.yafl.typeclass.Monad<ForOption, A> monad() {
        return new Monad<>(functor());
    }

    default org.nullpointeur.yafl.typeclass.Monad<ForOption, A> instanceMonad() {
        return monad();
    }

    final class ForOption {
        private ForOption() {
        }
    }

    record Functor<A>() implements org.nullpointeur.yafl.typeclass.Functor<ForOption, A> {
        @Override
        public <B> Kind<Option.ForOption, B> map(Kind<Option.ForOption, A> v, Function<A, B> f) {
            return switch ((Option<A>) v) {
                case Option.Some<A> some -> Option.some(f.apply(some.value()));
                case Option.None<A> ignored -> Option.none();
            };
        }
    }

    record Applicative<A>(
            org.nullpointeur.yafl.typeclass.Functor<ForOption, A> functor) implements org.nullpointeur.yafl.typeclass.Applicative<ForOption, A> {

        @Override
        public <B> Kind<ForOption, B> pure(B value) {
            return Option.some(value);
        }

        @Override
        public <B> Function1<Kind<ForOption, A>, Kind<ForOption, B>> ap(Kind<ForOption, Function1<A, B>> fn) {
            return v -> switch ((Option<Function1<A, B>>) fn) {
                case Option.Some<Function1<A, B>> someFn -> functor.map(v, someFn.value());
                case Option.None<Function1<A, B>> ignored -> Option.none();
            };
        }
    }

    record Monad<A>(
            org.nullpointeur.yafl.typeclass.Functor<ForOption, A> functor) implements org.nullpointeur.yafl.typeclass.Monad<ForOption, A> {

        @Override
        public <B> Kind<ForOption, B> pure(B value) {
            return Option.some(value);
        }

        @Override
        public <B> Kind<ForOption, B> flatMap(Kind<ForOption, A> v, Function<A, Kind<ForOption, B>> f) {
            return switch ((Option<A>) v) {
                case Option.Some<A> some -> (Option<B>) f.apply(some.value());
                case Option.None<A> ignored -> Option.none();
            };
        }
    }
}
