package pl.magzik.picture_comparer_fx.state.base;

import org.jetbrains.annotations.NotNull;

public interface State<C> {
    default void enter(@NotNull C context) {}
    default void exit(@NotNull C context) {}
}
