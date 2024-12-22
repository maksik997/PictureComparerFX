package pl.magzik.picture_comparer_fx.state;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;

/**
 * Represents the state after the file loading process is completed.
 * Updates the UI with the number of original and duplicate files.
 * Additionally, it disables certain buttons if duplicates are found, allowing the user to proceed with actions.
 */
public class ComparerLoadCompletedState extends ComparerPostProcessState {

    private final int originals;
    private final int duplicates;

    public ComparerLoadCompletedState(int originals, int duplicates) {
        this.originals = originals;
        this.duplicates = duplicates;
    }

    @Override
    public void enter(@NotNull ComparerController context) {
        super.enter(context);

        if (duplicates > 0) {
            context.setButtonsState(false, context.getMoveButton(), context.getRemoveButton());
        }

        context.getOriginalSlice().setPieValue(originals);
        context.getDuplicateSlice().setPieValue(duplicates);
    }
}
