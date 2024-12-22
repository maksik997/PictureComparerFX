package pl.magzik.picture_comparer_fx.state;

import javafx.scene.Cursor;
import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;
import pl.magzik.picture_comparer_fx.state.base.State;

/**
 * Represents the state after file processing is completed.
 * In this state, the UI is updated to reflect the results, and the progress bar is set to complete.
 * The cursor returns to the default state, indicating that processing is finished.
 */
public class ComparerPostProcessState implements State<ComparerController> {

    @Override
    public void enter(@NotNull ComparerController context) {
        context.setButtonsState(false, context.getBackButton(), context.getResetButton());
        context.getTaskProgressBar().setProgress(1);

        context.getStateText().setText(context.translate(ComparerController.StatePhase.DONE.toString()));
        context.getStage().getScene().setCursor(Cursor.DEFAULT);
    }
}
