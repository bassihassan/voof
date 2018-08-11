package me.bassihassan.core;

import java.util.Collection;

@FunctionalInterface
public interface Callback<T> {
    void apply(T t, Collection<CellError> errors);
}
