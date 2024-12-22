package pl.magzik.picture_comparer_fx.state;

import javafx.scene.Cursor;
import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.controller.ComparerController;
import pl.magzik.picture_comparer_fx.state.base.State;

public class ComparerLoadingState implements State<ComparerController> {

    @Override
    public void enter(@NotNull ComparerController context) {
        context.getStateText().setText(
            context.translate(ComparerController.States.PREPARE.toString())
        );

        context.getStage().getScene().setCursor(Cursor.WAIT);
    }
}
