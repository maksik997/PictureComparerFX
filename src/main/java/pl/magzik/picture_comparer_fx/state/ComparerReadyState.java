package pl.magzik.picture_comparer_fx.state;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;
import pl.magzik.picture_comparer_fx.state.base.State;

/**
 * Represents the state where the comparer is ready to start processing.
 * Certain buttons are disabled, signaling that the system is waiting for user input to load files.
 */
public class ComparerReadyState implements State<ComparerController> {
    @Override
    public void enter(@NotNull ComparerController context) {
        context.setButtonsState(false, context.getLoadButton());
    }
}
