package pl.magzik.picture_comparer_fx.state;

import javafx.scene.Cursor;
import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;
import pl.magzik.picture_comparer_fx.state.base.State;

/**
 * Represents the state during the file processing phase.
 * The UI disables interaction to prevent state changes and shows a waiting cursor to indicate ongoing tasks.
 * The progress bar enters an indeterminate state until the process is complete.
 */
public class ComparerProcessingState implements State<ComparerController> {

    private final String stateLabel;

    public ComparerProcessingState(@NotNull ComparerController.StatePhase state) {
        this.stateLabel = state.toString();
    }

    @Override
    public void enter(@NotNull ComparerController context) {
        context.setButtonsState(true,
                context.getBackButton(),
                context.getPathButton(),
                context.getLoadButton(),
                context.getMoveButton(),
                context.getRemoveButton(),
                context.getResetButton()
        );
        context.getTaskProgressBar().setProgress(-1);

        context.getStateText().setText(context.translate(stateLabel));

        context.getStage().getScene().setCursor(Cursor.WAIT);
    }
}
