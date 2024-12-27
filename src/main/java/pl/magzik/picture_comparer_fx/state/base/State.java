package pl.magzik.picture_comparer_fx.state.base;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a state within a state machine that operates on a given context.
 * <p>
 * Implementing classes define specific behaviors that should occur when the state is entered.
 * </p>
 *
 * @param <C> the type of context that this state operates on
 */
public interface State<C> {

    /**
     * Defines the actions to be performed when entering this state.
     * <p>
     * This method can be overridden by implementing classes to initialize or configure
     * the context appropriately. By default, this method performs no action.
     * </p>
     *
     * @param context the context associated with this state
     */
    default void enter(@NotNull C context) {}
}
