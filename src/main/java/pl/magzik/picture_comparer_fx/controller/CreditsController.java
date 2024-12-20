package pl.magzik.picture_comparer_fx.controller;

import pl.magzik.picture_comparer_fx.controller.base.PanelController;

public class CreditsController extends PanelController {

    public void handleHyperLink() {
        getHostServices().showDocument("https://github.com/maksik997");
    }
}
