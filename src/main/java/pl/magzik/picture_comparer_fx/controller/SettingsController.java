package pl.magzik.picture_comparer_fx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.controller.base.Controller;
import pl.magzik.picture_comparer_fx.controller.base.PanelController;
import pl.magzik.picture_comparer_fx.model.SettingsModel;
import pl.magzik.picture_comparer_fx.service.SettingsService;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/* TODO: JAVADOC */

public class SettingsController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

    private final SettingsModel model;

    private final SettingsService service;

    private boolean edited;

    public SettingsController() {
        this.model = Controller.getModel().getSettingsModel();
        try {
            this.service = new SettingsService(model);
        } catch (IOException e) {
            log.error("Couldn't load SettingsController class, because: {}", e.getMessage(), e);
            showErrorDialog("dialog.context.error.scene-switch");
            throw new RuntimeException(e);
        }

        this.edited = false;
    }

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private ComboBox<String> themeComboBox;

    @FXML
    private TextField moveDestinationTextField;

    @FXML
    private CheckBox recursiveModeCheckbox;

    @FXML
    private CheckBox perceptualHashCheckbox;

    @FXML
    private CheckBox pixelByPixelCheckbox;

    @FXML
    private TextField namePrefixTextField;

    @FXML
    private CheckBox lowercaseExtensionCheckbox;

    @FXML
    private Button resetButton;

    @FXML
    private Button saveButton;

    @Override
    public void setBundle(ResourceBundle bundle) {
        super.setBundle(bundle);

        languageComboBox.setItems(FXCollections.observableArrayList(
            SettingsModel.getLanguages().stream().map(this::translate).toList()
        ));
        themeComboBox.setItems(FXCollections.observableArrayList(
            SettingsModel.getThemes().stream().map(this::translate).toList()
        ));

        setValues();
    }

    @Override
    protected void backToMenu() {
        if (edited && !showConfirmationDialog("dialog.header.back-menu")) return;

        super.backToMenu();
    }

    private void setValues() {
        String language = translate(model.getLanguage());
        languageComboBox.setValue(language);

        String theme = translate(model.getTheme());
        themeComboBox.setValue(theme);

        moveDestinationTextField.setText(model.getMoveDestination());
        recursiveModeCheckbox.setSelected(model.isRecursiveMode());
        perceptualHashCheckbox.setSelected(model.isPerceptualHash());
        pixelByPixelCheckbox.setSelected(model.isPixelByPixel());
        namePrefixTextField.setText(model.getNamePrefix());
        lowercaseExtensionCheckbox.setSelected(model.isLowercaseExtension());
    }

    public void initialize() {
        setButtonsState(true, resetButton, saveButton);
    }

    @FXML
    public void handleChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(getStage());

        if (selectedDirectory != null) {
            moveDestinationTextField.setText(selectedDirectory.getAbsolutePath());
            handleChange();
            log.info("Selected directory: {}", selectedDirectory.getAbsolutePath());
        } else {
            log.info("No directory selected.");
        }
    }

    @FXML
    public void handleChange() {
        if (!edited) edited = true;
        setButtonsState(false, resetButton, saveButton);

        log.info("UI state has changed.");
    }

    @FXML
    public void handleReset() {
        setValues();
        edited = false;
        setButtonsState(true, resetButton, saveButton);

        log.info("Reset handled successfully.");
    }

    @FXML
    public void handleSave() {
        String oldLanguage = model.getLanguage();
        String oldTheme = model.getTheme();
        service.updateModel(
            findKey(languageComboBox.getValue()),
            findKey(themeComboBox.getValue()),
            moveDestinationTextField.getText(),
            recursiveModeCheckbox.isSelected(),
            perceptualHashCheckbox.isSelected(),
            pixelByPixelCheckbox.isSelected(),
            namePrefixTextField.getText(),
            lowercaseExtensionCheckbox.isSelected()
        );
        if (!model.getLanguage().equals(oldLanguage)) {
            Locale locale = Locale.forLanguageTag(model.getLanguage());
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
            setBundle(bundle);
            log.info("Resource Bundle switched successfully!");
        }

        // TODO: HANDLE THEME SWITCHING ( AFTER THEME: SYSTEM IS READY ).
        if (!model.getTheme().equals(oldTheme)) {
            setCurrentTheme(model.getTheme());
        }

        try {
            service.saveSettings();
        } catch (IOException e) {
            log.error("Couldn't save settings, because: {}", e.getMessage(), e);
            showErrorDialog("dialog.context.error.settings.save");
            return;
        }

        edited = false;
        setButtonsState(true, resetButton, saveButton);

        log.info("Saving settings, UI handled successfully");
        switchScene("/fxml/main-view.fxml");
    }
}
