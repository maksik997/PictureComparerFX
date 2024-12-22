package pl.magzik.picture_comparer_fx.state;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;
import pl.magzik.picture_comparer_fx.state.base.State;

public class ComparerReadyState implements State<ComparerController> {
    @Override
    public void enter(@NotNull ComparerController context) {
        context.setButtonsState(false, context.getLoadButton());
    }

    @Override
    public void exit(@NotNull ComparerController context) {
        context.setButtonsState(true,
            context.getBackButton(),
            context.getPathButton(),
            context.getLoadButton(),
            context.getMoveButton(),
            context.getRemoveButton(),
            context.getResetButton()
        );
    }
}
