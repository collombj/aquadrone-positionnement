package fr.onema.app.view;

import fr.onema.app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RootLayoutController {
    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    private Label testLabel;

    @FXML
    private void testAction() {
        testLabel.setText("Ceci est un test");
    }
}
