package pl.magzik._new.controller;

import pl.magzik._new.controller.base.PanelController;

public class CreditsController extends PanelController {

    public void handleHyperLink() {
        getHostServices().showDocument("https://github.com/maksik997");
    }
}
