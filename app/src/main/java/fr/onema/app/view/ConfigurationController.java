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
    private static double horizontalDefaultValue = 0;
    private static double verticalDefaultValue = 0;
    private static double depthDefaultValue = 0;

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
    private void initialize() {
        insertSpinnerValues();
    }

    @FXML
    private void insertSpinnerValues() {
        horizontalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(horizontalSlider.getMin(), horizontalSlider.getMax(), horizontalDefaultValue, horizontalSlider.getMinorTickCount()));
        verticalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(verticalSlider.getMin(), verticalSlider.getMax(), verticalDefaultValue, verticalSlider.getMinorTickCount()));
        depthSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(depthSlider.getMin(), depthSlider.getMax(), depthDefaultValue, depthSlider.getMinorTickCount()));
    }

    /***
     * Permet de remettre à 0 les Spinners et Sliders de la vue de configuration
     */
    @FXML
    private void resetConfigurationLayout() {
        horizontalSlider.adjustValue(horizontalDefaultValue);
        verticalSlider.adjustValue(verticalDefaultValue);
        depthSlider.adjustValue(depthDefaultValue);
        updateSpinner(horizontalSpinner, horizontalDefaultValue);
        updateSpinner(verticalSpinner, verticalDefaultValue);
        updateSpinner(depthSpinner, depthDefaultValue);
    }

    /***
     * Binding des valuers Spinner -> Slider
     */
    @FXML
    private void updateSliderFromSpinner() {
        horizontalSlider.adjustValue(horizontalSpinner.getValue());
        verticalSlider.adjustValue(verticalSpinner.getValue());
        depthSlider.adjustValue(depthSpinner.getValue());
    }

    /***
     * Binding des valuers Slider -> Spinner
     */
    @FXML
    private void updateSpinnerFromSlider() {
        updateSpinner(horizontalSpinner, horizontalSlider.getValue());
        updateSpinner(verticalSpinner, verticalSlider.getValue());
        updateSpinner(depthSpinner, depthSlider.getValue());
    }

    private void updateSpinner(Spinner sp, Double d) {
        SpinnerValueFactory<Double> valueFactory = sp.getValueFactory();
        if (valueFactory != null) {
            StringConverter<Double> converter = valueFactory.getConverter();
            if (converter != null) {
                try{
                    valueFactory.setValue(d);
                } catch(NumberFormatException nfe){
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
        // TODO : do something with values
        double horizontalOffset = horizontalSpinner.getValue();
        double verticalOffset = verticalSpinner.getValue();
        double depthOffset = depthSpinner.getValue();
    }
}
