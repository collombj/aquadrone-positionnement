package fr.onema.app.view;

import fr.onema.app.model.CheckDependenciesAvailabilityTask;
import fr.onema.lib.tools.Configuration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;

/***
 * Controlleur associé à la vue RootLayout.fxml
 */
public class RootLayoutController {
    private final Configuration c;

    @FXML
    private TitledPane sensorsTitledPane;

    @FXML
    private Button configurationButton;

    @FXML
    private Label dbLabel;

    @FXML
    private Label mavlinkLabel;

    /***
     * Constructeur du controlleur du RootLayout
     * @param c Fichier de configuration comprenant les infos de la base Postgres
     */
    public RootLayoutController(Configuration c) {
        this.c = Objects.requireNonNull(c);
    }

    public Configuration getConfiguration() {
        return c;
    }

    /***
     * Méthode permettant de déclarer le scheduler de tâches pour interroger l'état des dépendances
     */
    @FXML
    private void initialize() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new CheckDependenciesAvailabilityTask(this), 0, 30_000);
    }

    @FXML
    public void updateDatabaseColor(Color c) {
        dbLabel.setTextFill(c);
    }

    @FXML
    public void updateMavlinkColor(Color c) {
        mavlinkLabel.setTextFill(c);
    }

    /***
     * Permet de redimensionner dynamiquement les éléments de la vue lorsque certaines parties sont réduites
     */
    @FXML
    private void resizeParent() {
        Stage root = (Stage)sensorsTitledPane.getScene().getWindow();
        root.sizeToScene();
        root.show();
    }

    /***
     * Permet de faire apparaitre le popup de configuration
     * @param event
     * @throws IOException
     */
    @FXML
    private void accessConfiguration(ActionEvent event) throws IOException {
        Stage stage;
        Stage root = (Stage)configurationButton.getScene().getWindow();

        if (event.getSource() == configurationButton) {
            stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("ConfigurationLayout.fxml"))));
            stage.setTitle("Configuration");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(root);
            stage.resizableProperty().set(false);
            stage.showAndWait();
        } else {
            stage = root;
            stage.close();
        }
    }
}
