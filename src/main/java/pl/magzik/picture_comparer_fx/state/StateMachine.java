package pl.magzik.picture_comparer_fx.state;

import pl.magzik.picture_comparer_fx.state.base.State;

public class StateMachine<S extends State<C>, C> {
    private S currentState;
    private final C context;

    public StateMachine(C context) {
        this.context = context;
    }

    public void changeState(S newState) {
        if (currentState != null) {
            currentState.exit(context);
        }
        currentState = newState;
        currentState.enter(context);
    }

    public S getCurrentState() {
        return currentState;
    }
}
