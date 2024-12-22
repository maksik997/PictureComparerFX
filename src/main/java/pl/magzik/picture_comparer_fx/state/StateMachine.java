package pl.magzik.picture_comparer_fx.state;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.state.base.State;

/**
 * A simple state machine that manages transitions between states.
 * <p>
 * This state machine operates on a given context and handles transitions by invoking
 * the appropriate state's {@link State#enter(C)} method upon state changes.
 * </p>
 *
 * @param <S> the type of state, which must implement {@link State<C>}
 * @param <C> the type of context that the state machine operates on
 */
public class StateMachine<S extends State<C>, C> {
    private S currentState;
    private final C context;

    /**
     * Constructs a state machine with the specified context.
     *
     * @param context the context that the state machine will operate on
     */
    public StateMachine(@NotNull C context) {
        this.context = context;
    }

    /**
     * Transitions the state machine to a new state.
     * <p>
     * This method updates the current state and triggers the {@code enter} method
     * of the new state, passing the context to it.
     * </p>
     *
     * @param newState the state to transition to
     */
    public void changeState(@NotNull S newState) {
        currentState = newState;
        currentState.enter(context);
    }

    /**
     * Retrieves the current state of the state machine.
     *
     * @return the current state
     */
    public @NotNull S getCurrentState() {
        return currentState;
    }
}
