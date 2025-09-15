package org.nullpointeur.yafl;

import org.nullpointeur.yafl.function.Function1;

public sealed interface Try<A> {

    record Success<A>(A value) implements Try<A> {}

    record Failure<A>(Throwable error) implements Try<A> {}


    static <A> Try<A> success(A value) {
        return new Success<>(value);
    }

    static <A> Try<A> failure(Throwable error) {
        return new Failure<>(error);
    }

    default <B> Try<B> map(Function1<A, B> mapper) {
        return switch (this) {
            case Success<A> s -> success(mapper.apply(s.value));
            case Failure<A> f -> failure(f.error);
        };
    }

    default <B> Function1<Try<A>, Try<B>> ap(Try<Function1<A, B>> fn) {
        return v -> switch (fn) {
            case Success<Function1<A, B>> sFn -> v.map(sFn.value);
            case Failure<Function1<A, B>> fFn -> failure(fFn.error);
        };
    }

    default <B> Try<B> apply(Try<Function1<A, B>> fn) {
        return switch (fn) {
            case Success<Function1<A, B>> sFn -> this.map(sFn.value);
            case Failure<Function1<A, B>> fFn -> failure(fFn.error);
        };
    }

    default <B> Try<B> flatMap(Function1<A, Try<B>> mapper) {
        return switch (this) {
            case Success<A> s -> mapper.apply(s.value);
            case Failure<A> f -> failure(f.error);
        };
    }

    default <B> B fold(Function1<Throwable, B> ifFailure, Function1<A, B> ifSuccess) {
        return switch (this) {
            case Success<A> s -> ifSuccess.apply(s.value);
            case Failure<A> f -> ifFailure.apply(f.error);
        };
    }

    default <L> Either<L, A> toEither(Function1<Throwable, L> leftMapper) {
        return switch (this) {
            case Success<A> s -> Either.right(s.value);
            case Failure<A> f -> Either.left(leftMapper.apply(f.error));
        };
    }

    default Option<A> toOption() {
        return switch (this) {
            case Success<A> s -> Option.some(s.value);
            case Failure<A> f -> Option.none();
        };
    }

    default void peek(Function1<A, Void> consumer) {
        if (this instanceof Success<A>(A value)) {
            consumer.apply(value);
        }
    }

    default Try<A> recover(Function1<Throwable, A> f) {
        return switch (this) {
            case Success<A> s -> s;
            case Failure<A> fErr -> success(f.apply(fErr.error));
        };
    }

    default Try<A> recoverWith(Function1<Throwable, Try<A>> f) {
        return switch (this) {
            case Success<A> s -> s;
            case Failure<A> fErr -> f.apply(fErr.error);
        };
    }
}
