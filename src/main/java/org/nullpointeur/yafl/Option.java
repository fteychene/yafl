package org.nullpointeur.yafl;

import org.nullpointeur.yafl.kind.Kind;
import org.nullpointeur.yafl.typeclass.Applicative;
import org.nullpointeur.yafl.typeclass.Functor;
import org.nullpointeur.yafl.typeclass.Monad;

import java.util.function.Function;

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

    default <B> Option<B> map(java.util.function.Function<A, B> f) {
        return (Option<B>) instanceFunctor().map(this, f);
    }

    default <B> Option<B> ap(Option<Function<A, B>> f) {
        return (Option<B>) instanceApplicative().ap(f, this);
    }

    default <B> Option<B> flatMap(java.util.function.Function<A, Option<B>> f) {
        return (Option<B>) instanceMonad().flatMap(this, f.andThen(kind -> (Option<B>) kind));
    }

    static <A, B> Function<Option<A>, Option<B>> ap(java.util.function.Function<A, B> f) {
        Applicative<ForOption, A> applicative = applicative();
        return a -> (Option<B>) applicative.ap(applicative.pure(f), a);
    }

    // Plumbing for type classes

    static <A> Functor<ForOption, A> functor() {
        return new OptionFunctor<>();
    }

    default Functor<ForOption, A> instanceFunctor() {
        return functor();
    }

    static <A> Applicative<ForOption, A> applicative() {
        return new OptionApplicative<>(functor());
    }

    default Applicative<ForOption, A> instanceApplicative() {
        return applicative();
    }

    static <A> Monad<ForOption, A> monad() {
        return new OptionMonad<>(functor());
    }

    default Monad<ForOption, A> instanceMonad() {
        return monad();
    }

    final class ForOption {
        private ForOption() {
        }
    }

    record OptionFunctor<A>() implements Functor<Option.ForOption, A> {
        @Override
        public <B> Kind<Option.ForOption, B> map(Kind<Option.ForOption, A> v, Function<A, B> f) {
            return switch ((Option<A>) v) {
                case Option.Some<A> some -> Option.some(f.apply(some.value()));
                case Option.None<A> ignored -> Option.none();
            };
        }
    }

    record OptionApplicative<A>(Functor<ForOption, A> functor) implements Applicative<ForOption, A> {

        @Override
        public <B> Kind<ForOption, B> pure(B value) {
            return Option.some(value);
        }

        @Override
        public <B> Function<Kind<ForOption, A>, Kind<ForOption, B>> ap(Kind<ForOption, Function<A, B>> fn) {
            return v -> switch ((Option<Function<A, B>>) fn) {
                case Option.Some<Function<A, B>> someFn -> functor.map(v, someFn.value());
                case Option.None<Function<A, B>> ignored -> Option.none();
            };
        }
    }

    record OptionMonad<A>(Functor<ForOption, A> functor) implements org.nullpointeur.yafl.typeclass.Monad<ForOption, A> {

        @Override
        public <B> Kind<ForOption, B> flatMap(Kind<ForOption, A> v, Function<A, Kind<ForOption, B>> f) {
            return switch ((Option<A>) v) {
                case Option.Some<A> some -> (Option<B>) f.apply(some.value());
                case Option.None<A> ignored -> Option.none();
            };
        }
    }
}
