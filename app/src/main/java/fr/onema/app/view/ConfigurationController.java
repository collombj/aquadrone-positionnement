package fr.onema.app.view;

import fr.onema.app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Objects;

/**
 * Created by you on 13/02/2017.
 */

/***
 * Controlleur associé à la vue ConfigurationLayout.fxml
 */
public class ConfigurationController {
    private RootLayoutController parent;

    @FXML
    private Slider verticalSlider;

    @FXML
    private Slider horizontalSlider;

    @FXML
    private Slider depthSlider;

    @FXML
    private Spinner<Double> verticalSpinner;

    @FXML
    private Spinner<Double> horizontalSpinner;

    @FXML
    private Spinner<Double> depthSpinner;

    @FXML
    private Button applyButton;

    /***
     * Méthode permettant l'initialisation des valeurs dans les spinners
     */
    @FXML
    public void initialize() {

    }

    public void init(RootLayoutController parent) {
        this.parent = Objects.requireNonNull(parent);
    }

    @FXML
    public void insertSpinnerValues(double h, double v, double d) {
        horizontalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(horizontalSlider.getMin(), horizontalSlider.getMax(), h, horizontalSlider.getMinorTickCount()));
        verticalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(verticalSlider.getMin(), verticalSlider.getMax(), v, verticalSlider.getMinorTickCount()));
        depthSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(depthSlider.getMin(), depthSlider.getMax(), d, depthSlider.getMinorTickCount()));
        horizontalSlider.adjustValue(horizontalSpinner.getValue());
        verticalSlider.adjustValue(verticalSpinner.getValue());
        depthSlider.adjustValue(depthSpinner.getValue());
    }

    /***
     * Permet de remettre à 0 les Spinners et Sliders de la vue de configuration
     */
    @FXML
    public void resetConfigurationLayout() {
        horizontalSlider.adjustValue(Main.HORIZONTAL_DEFAULT_VALUE);
        verticalSlider.adjustValue(Main.VERTICAL_DEFAULT_VALUE);
        depthSlider.adjustValue(Main.DEPTH_DEFAULT_VALUE);
        updateSpinner(horizontalSpinner, Main.HORIZONTAL_DEFAULT_VALUE);
        updateSpinner(verticalSpinner, Main.VERTICAL_DEFAULT_VALUE);
        updateSpinner(depthSpinner, Main.DEPTH_DEFAULT_VALUE);
    }

    /***
     * Binding des valuers Spinner -> Slider horizontal
     */
    @FXML
    private void updateSliderFromSpinnerH() {
        horizontalSlider.adjustValue(horizontalSpinner.getValue());
        parent.setHorizontalOffset(horizontalSpinner.getValue());
    }

    /***
     * Binding des valuers Spinner -> Slider vertical
     */
    @FXML
    private void updateSliderFromSpinnerV() {
        verticalSlider.adjustValue(verticalSpinner.getValue());
        parent.setVerticalOffset(verticalSpinner.getValue());
    }

    /***
     * Binding des valuers Spinner -> Slider depth
     */
    @FXML
    private void updateSliderFromSpinnerD() {
        depthSlider.adjustValue(depthSpinner.getValue());
        parent.setDepthOffset(depthSpinner.getValue());
    }

    /***
     * Binding des valuers Slider -> Spinner horizontal
     */
    @FXML
    private void updateSpinnerFromSliderH() {
        updateSpinner(horizontalSpinner, horizontalSlider.getValue());
        parent.setHorizontalOffset(horizontalSpinner.getValue());
    }

    /***
     * Binding des valuers Slider -> Spinner vertical
     */
    @FXML
    private void updateSpinnerFromSliderV() {
        updateSpinner(verticalSpinner, verticalSlider.getValue());
        parent.setVerticalOffset(verticalSpinner.getValue());
    }

    /***
     * Binding des valuers Slider -> Spinner depth
     */
    @FXML
    private void updateSpinnerFromSliderD() {
        updateSpinner(depthSpinner, depthSlider.getValue());
        parent.setDepthOffset(depthSpinner.getValue());
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
        parent.setHorizontalOffset(horizontalSpinner.getValue());
        parent.setVerticalOffset(verticalSpinner.getValue());
        parent.setDepthOffset(depthSpinner.getValue());
        Stage stage = (Stage) applyButton.getScene().getWindow();
        stage.close();
    }
}
