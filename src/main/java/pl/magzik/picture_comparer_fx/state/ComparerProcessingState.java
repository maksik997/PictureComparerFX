package pl.magzik.picture_comparer_fx.state;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;
import pl.magzik.picture_comparer_fx.state.base.State;

public class ComparerProcessingState implements State<ComparerController> {
    @Override
    public void enter(@NotNull ComparerController context) {
        context.getStateText().setText(
            context.translate(ComparerController.States.MAP.toString())
        );
    }
}
