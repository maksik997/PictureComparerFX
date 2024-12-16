package pl.magzik._new.model.base;

import pl.magzik._new.model.ComparerModel;
import pl.magzik._new.model.SettingsModel;

public class Model {

    private final ComparerModel comparerModel;

    private final SettingsModel settingsModel;

    public Model() {
        this.comparerModel = new ComparerModel();
        this.settingsModel = new SettingsModel();
    }

    public ComparerModel getComparerModel() {
        return comparerModel;
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }
}
