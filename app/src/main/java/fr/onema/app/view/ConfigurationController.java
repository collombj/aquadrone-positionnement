package fr.onema.app.view;

import fr.onema.app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/***
 * Controller associé à la vue ConfigurationLayout.fxml
 */
public class ConfigurationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class.getName());
    private RootLayoutController parent;
    private Main main;

    @FXML
    private Slider yOffsetSlider;

    @FXML
    private Slider xOffsetSlider;

    @FXML
    private Slider zOffsetSlider;

    @FXML
    private Spinner<Double> yOffsetSpinner;

    @FXML
    private Spinner<Double> xOffsetSpinner;

    @FXML
    private Spinner<Double> zOffsetSpinner;

    @FXML
    private Button applyButton;

    /***
     * Méthode permettant l'initialisation des valeurs dans les spinners
     */
    @FXML
    void initialize() {
        // doit rester vide
    }

    void init(RootLayoutController parent, Main main) {
        this.parent = Objects.requireNonNull(parent);
        this.main = Objects.requireNonNull(main);
    }

    @FXML
    void insertSpinnerValues(double x, double y, double z) {
        xOffsetSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(xOffsetSlider.getMin(), xOffsetSlider.getMax(), x, xOffsetSlider.getMinorTickCount()));
        yOffsetSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(yOffsetSlider.getMin(), yOffsetSlider.getMax(), y, yOffsetSlider.getMinorTickCount()));
        zOffsetSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(zOffsetSlider.getMin(), zOffsetSlider.getMax(), z, zOffsetSlider.getMinorTickCount()));
        xOffsetSlider.adjustValue(xOffsetSpinner.getValue());
        yOffsetSlider.adjustValue(yOffsetSpinner.getValue());
        zOffsetSlider.adjustValue(zOffsetSpinner.getValue());
    }

    /***
     * Permet de remettre à 0 les Spinners et Sliders de la vue de configuration
     */
    @FXML
    public void resetConfigurationLayout() {
        xOffsetSlider.adjustValue(Main.X_DEFAULT_VALUE);
        yOffsetSlider.adjustValue(Main.Y_DEFAULT_VALUE);
        zOffsetSlider.adjustValue(Main.Z_DEFAULT_VALUE);
        updateSpinner(xOffsetSpinner, Main.X_DEFAULT_VALUE);
        updateSpinner(yOffsetSpinner, Main.Y_DEFAULT_VALUE);
        updateSpinner(zOffsetSpinner, Main.Z_DEFAULT_VALUE);
    }

    /***
     * Binding des valeurs Spinner -> Slider X
     */
    @FXML
    private void updateSliderFromSpinnerX() {
        xOffsetSlider.adjustValue(xOffsetSpinner.getValue());
        parent.setOffsetX(xOffsetSpinner.getValue());
    }

    /***
     * Binding des valeurs Spinner -> Slider Y
     */
    @FXML
    private void updateSliderFromSpinnerY() {
        yOffsetSlider.adjustValue(yOffsetSpinner.getValue());
        parent.setOffsetY(yOffsetSpinner.getValue());
    }

    /***
     * Binding des valeurs Spinner -> Slider Z
     */
    @FXML
    private void updateSliderFromSpinnerZ() {
        zOffsetSlider.adjustValue(zOffsetSpinner.getValue());
        parent.setOffsetZ(zOffsetSpinner.getValue());
    }

    /***
     * Binding des valeurs Slider -> Spinner X
     */
    @FXML
    private void updateSpinnerFromSliderX() {
        updateSpinner(xOffsetSpinner, xOffsetSlider.getValue());
        parent.setOffsetX(xOffsetSpinner.getValue());
    }

    /***
     * Binding des valeurs Slider -> Spinner Y
     */
    @FXML
    private void updateSpinnerFromSliderY() {
        updateSpinner(yOffsetSpinner, yOffsetSlider.getValue());
        parent.setOffsetY(yOffsetSpinner.getValue());
    }

    /***
     * Binding des valeurs Slider -> Spinner Z
     */
    @FXML
    private void updateSpinnerFromSliderZ() {
        updateSpinner(zOffsetSpinner, zOffsetSlider.getValue());
        parent.setOffsetZ(zOffsetSpinner.getValue());
    }

    private void updateSpinner(Spinner<Double> sp, Double d) {
        SpinnerValueFactory<Double> valueFactory = sp.getValueFactory();
        if (valueFactory != null) {
            StringConverter<Double> converter = valueFactory.getConverter();
            if (converter != null) {
                try {
                    valueFactory.setValue(d);
                } catch (NumberFormatException nfe) {
                    sp.getEditor().setText(converter.toString(valueFactory.getValue()));
                }
            }
        }
    }

    /***
     * Apply settings to the next dive operation
     */
    @FXML
    private void applyConfigurationOnNextDive() {
        parent.setOffsetX(xOffsetSpinner.getValue());
        parent.setOffsetY(yOffsetSpinner.getValue());
        parent.setOffsetZ(zOffsetSpinner.getValue());
        try {
            main.getConfiguration().setCorrection(xOffsetSpinner.getValue(), yOffsetSpinner.getValue(), zOffsetSpinner.getValue());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
            return;
        }
        Stage stage = (Stage) applyButton.getScene().getWindow();
        stage.close();
    }
}
