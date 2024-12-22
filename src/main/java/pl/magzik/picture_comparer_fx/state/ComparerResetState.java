package pl.magzik.picture_comparer_fx.state;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;
import pl.magzik.picture_comparer_fx.state.base.State;

/**
 * Represents the reset state of the comparer.
 * This state resets the UI, clears file paths, and sets all controls to their default positions.
 * It prepares the application for a new file comparison session.
 */
public class ComparerResetState implements State<ComparerController> {

    private static final String DEFAULT_TRAY_STATE = "0";
    private static final int DEFAULT_PIE_VALUE = 50;

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
            context.translate(ComparerController.StatePhase.READY.toString())
        );

        context.getOriginalTrayTextField().setText(DEFAULT_TRAY_STATE);
        context.getDuplicateTrayTextField().setText(DEFAULT_TRAY_STATE);

        context.getOriginalSlice().setPieValue(DEFAULT_PIE_VALUE);
        context.getDuplicateSlice().setPieValue(DEFAULT_PIE_VALUE);

        context.getPathTextField().clear();
    }
}
