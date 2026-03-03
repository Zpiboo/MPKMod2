package io.github.kurrycat.mpkmod.util;

import java.util.Objects;

public class Tuple<A, B> implements Copyable<Tuple<A, B>> {
    private A a;
    private B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getFirst() {
        return this.a;
    }

    public void setFirst(A a) {
        this.a = a;
    }

    public B getSecond() {
        return this.b;
    }

    public void setSecond(B b) {
        this.b = b;
    }

    @SuppressWarnings("unchecked")
    public Tuple<A, B> copy() {
        return new Tuple<>(
                this.a instanceof Copyable ? ((Copyable<A>) this.a).copy() : this.a,
                this.b instanceof Copyable ? ((Copyable<B>) this.b).copy() : this.b
        );
    }

    @Override
    public String toString() {
        return "Tuple{" + a + ", " + b + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> that = (Tuple<?, ?>) o;

        return Objects.equals(a, that.a) && Objects.equals(b, that.b);
    }
}
