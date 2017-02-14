package fr.onema.app.view;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

/**
 * Created by you on 13/02/2017.
 */

/***
 * Controlleur associé à la vue ConfigurationLayout.fxml
 */
public class ConfigurationController {
    private static final double HORIZONTAL_DEFAULT_VALUE = 0;
    private static final double VERTICAL_DEFAULT_VALUE = 0;
    private static final double DEPTH_DEFAULT_VALUE = 0;

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

    /***
     * Méthode permettant l'initialisation des valeurs dans les spinners
     */
    @FXML
    public void initialize() {
        insertSpinnerValues();
    }

    @FXML
    private void insertSpinnerValues() {
        horizontalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(horizontalSlider.getMin(), horizontalSlider.getMax(), HORIZONTAL_DEFAULT_VALUE, horizontalSlider.getMinorTickCount()));
        verticalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(verticalSlider.getMin(), verticalSlider.getMax(), VERTICAL_DEFAULT_VALUE, verticalSlider.getMinorTickCount()));
        depthSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(depthSlider.getMin(), depthSlider.getMax(), DEPTH_DEFAULT_VALUE, depthSlider.getMinorTickCount()));
    }

    /***
     * Permet de remettre à 0 les Spinners et Sliders de la vue de configuration
     */
    @FXML
    public void resetConfigurationLayout() {
        horizontalSlider.adjustValue(HORIZONTAL_DEFAULT_VALUE);
        verticalSlider.adjustValue(VERTICAL_DEFAULT_VALUE);
        depthSlider.adjustValue(DEPTH_DEFAULT_VALUE);
        updateSpinner(horizontalSpinner, HORIZONTAL_DEFAULT_VALUE);
        updateSpinner(verticalSpinner, VERTICAL_DEFAULT_VALUE);
        updateSpinner(depthSpinner, DEPTH_DEFAULT_VALUE);
    }

    /***
     * Binding des valuers Spinner -> Slider horizontal
     */
    @FXML
    private void updateSliderFromSpinnerH() {
        horizontalSlider.adjustValue(horizontalSpinner.getValue());
    }

    /***
     * Binding des valuers Spinner -> Slider vertical
     */
    private void updateSliderFromSpinnerV() {
        verticalSlider.adjustValue(verticalSpinner.getValue());
    }

    /***
     * Binding des valuers Spinner -> Slider depth
     */
    private void updateSliderFromSpinnerD() {
        depthSlider.adjustValue(depthSpinner.getValue());
    }

    /***
     * Binding des valuers Slider -> Spinner horizontal
     */
    @FXML
    private void updateSpinnerFromSliderH() {
        updateSpinner(horizontalSpinner, horizontalSlider.getValue());
    }

    /***
     * Binding des valuers Slider -> Spinner vertical
     */
    @FXML
    private void updateSpinnerFromSliderV() {
        updateSpinner(verticalSpinner, verticalSlider.getValue());
    }

    /***
     * Binding des valuers Slider -> Spinner depth
     */
    @FXML
    private void updateSpinnerFromSliderD() {
        updateSpinner(depthSpinner, depthSlider.getValue());
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
        /* TODO : do something with values
        double horizontalOffset = horizontalSpinner.getValue();
        double verticalOffset = verticalSpinner.getValue();
        double depthOffset = depthSpinner.getValue();
         */

    }
}
