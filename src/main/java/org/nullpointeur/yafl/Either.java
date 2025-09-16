package org.nullpointeur.yafl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Either<L, R> {

    record Left<L, R>(L value) implements Either<L, R> {
    }

    record Right<L, R>(R value) implements Either<L, R> {
    }

    static <L, R> Either<L, R> right(R r) {
        return new Right<>(r);
    }

    static <L, R> Either<L, R> left(L l) {
        return new Left<>(l);
    }

    default <B> Either<L, B> map(Function<R, B> f) {
        return switch (this) {
            case Left<L, R> left -> new Left<>(left.value());
            case Right<L, R> right -> new Right<>(f.apply(right.value()));
        };
    }

    default <B> Either<B, R> mapLeft(Function<L, B> f) {
        return switch (this) {
            case Left<L, R> left -> new Left<>(f.apply(left.value()));
            case Right<L, R> right -> new Right<>(right.value());
        };
    }

    default <B> Either<L, B> flatMap(Function<R, Either<L, B>> f) {
        return switch (this) {
            case Left<L, R> left -> new Left<>(left.value());
            case Right<L, R> right -> f.apply(right.value());
        };
    }

    default <B> Either<L, B> apply(Either<L, Function<R, B>> fn) {
        return switch (fn) {
            case Left<L, Function<R, B>> left -> new Left<>(left.value());
            case Right<L, Function<R, B>> right -> map(right.value());
        };
    }

    default <B> B fold(Function<L, B> ifLeft, Function<R, B> ifRight) {
        return switch (this) {
            case Left<L, R> left -> ifLeft.apply(left.value());
            case Right<L, R> right -> ifRight.apply(right.value());
        };
    }

    default void peek(Consumer<R> consumer) {
        if (this instanceof Right<L, R>(R value)) {
            consumer.accept(value);
        }
    }

    default void peekLeft(Consumer<L> consumer) {
        if (this instanceof Left<L, R>(L value)) {
            consumer.accept(value);
        }
    }

    default Either<R, L> swap() {
        return fold(Either::right, Either::left);
    }

    default Either<L, R> recover(Function<L, R> f) {
        return switch (this) {
            case Left<L, R> left -> new Right<>(f.apply(left.value()));
            case Right<L, R> right -> right;
        };
    }

    default Either<L, R> recoverWith(Function<L, Either<L, R>> f) {
        return switch (this) {
            case Left<L, R> left -> f.apply(left.value());
            case Right<L, R> right -> right;
        };
    }

    default R getOrElse(R other) {
        return getOrElse(() -> other);
    }

    default R getOrElse(Supplier<R> other) {
        return getOrElse(ignored -> other.get());
    }

    default R getOrElse(Function<L, R> other) {
        return fold(other, r -> r);
    }

    default Option<R> toOption() {
        return fold(l -> Option.none(), Option::some);
    }

}
