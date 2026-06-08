package com.stump.genshinstrument_lm.util;

public record TriValue<A, B, C>(A obj1, B obj2, C obj3) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TriValue<?, ?, ?> other)) return false;

        return java.util.Objects.equals(obj1, other.obj1)
                && java.util.Objects.equals(obj2, other.obj2)
                && java.util.Objects.equals(obj3, other.obj3);
    }
}