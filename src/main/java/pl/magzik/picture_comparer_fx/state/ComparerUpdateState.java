package pl.magzik.picture_comparer_fx.state;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;

/**
 * Represents a state that updates the UI with the results of a file comparison.
 * This state updates the count of original and duplicate files, reflecting the current processing phase.
 */
public class ComparerUpdateState extends ComparerProcessingState {

    private final int total;
    private final int duplicates;

    public ComparerUpdateState(@NotNull ComparerController.StatePhase state, int total, int duplicates) {
        super(state);
        this.total = total;
        this.duplicates = duplicates;
    }

    @Override
    public void enter(@NotNull ComparerController context) {
        super.enter(context);

        context.getOriginalTrayTextField().setText(String.valueOf(total));
        context.getDuplicateTrayTextField().setText(String.valueOf(duplicates));
    }
}
