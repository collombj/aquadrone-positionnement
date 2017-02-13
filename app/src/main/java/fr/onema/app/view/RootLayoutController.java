package fr.onema.app.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class RootLayoutController {

    @FXML
    private Button configurationButton;

    @FXML
    private void testAction(ActionEvent event) throws IOException {
        Stage stage;
        Stage root;
        root = (Stage)configurationButton.getScene().getWindow();

        if (event.getSource() == configurationButton) {
            stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("ConfigurationLayout.fxml"))));
            stage.setTitle("Configuration");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(root);
            stage.showAndWait();
        } else {
            stage = root;
            stage.close();
        }
    }
}
