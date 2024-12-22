package pl.magzik.picture_comparer_fx.state;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;
import pl.magzik.picture_comparer_fx.state.base.State;

public class ComparerResetState implements State<ComparerController> {
    @Override
    public void enter(@NotNull ComparerController context) {
        context.setButtonsState(true,
            context.getMoveButton(),
            context.getRemoveButton(),
            context.getResetButton(),
            context.getLoadButton()
        );
        context.setButtonsState(false,
            context.getBackButton(),
            context.getPathButton()
        );

        context.getTaskProgressBar().setProgress(1);
        context.getStateText().setText(
            context.translate(ComparerController.States.READY.toString())
        );

        context.getOriginalTrayTextField().setText("0");
        context.getDuplicateTrayTextField().setText("0");

        context.getPathTextField().clear();
    }
}
