package fr.onema.app.view;

import fr.onema.app.model.CheckDependenciesAvailabilityTask;
import fr.onema.lib.tools.Configuration;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
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
    private double horizontalOffset = 0;
    private double verticalOffset = 0;
    private double depthOffset = 0;
    private boolean isRunning;
    private final Configuration c;

    @FXML
    private TitledPane sensorsTitledPane;

    @FXML
    private Button configurationButton;

    @FXML
    private Label dbLabel;

    @FXML
    private Label mavlinkLabel;

    @FXML
    private Button runButton;

    @FXML
    private ProgressIndicator progressIndicator;

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
        progressIndicator.setVisible(false);
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
        Stage root = (Stage) sensorsTitledPane.getScene().getWindow();
        root.sizeToScene();
        root.show();
    }

    /***
     * Permet de faire apparaitre le popup de configuration
     * @param event Le contrôle ayant appelé la méthode
     * @throws IOException
     */
    @FXML
    private void accessConfiguration(ActionEvent event) throws IOException {
        Stage stage;
        Stage root = (Stage) configurationButton.getScene().getWindow();

        if (event.getSource() == configurationButton) {
            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigurationLayout.fxml"));
            Region region = loader.load();
            Scene scene = new Scene(region);
            stage.setTitle("Configuration");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.initOwner(root);
            stage.setAlwaysOnTop(true);
            ConfigurationController controller = loader.getController();
            controller.initialize();
            controller.insertSpinnerValues(horizontalOffset, verticalOffset, depthOffset);
            controller.init(this);
            stage.showAndWait();
        } else {
            stage = root;
            stage.close();
        }
    }

    public void setHorizontalOffset(double horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
    }

    public void setVerticalOffset(double verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    public void setDepthOffset(double depthOffset) {
        this.depthOffset = depthOffset;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @FXML
    private void executeMeasures() {
        setRunning(true);
        handleRunningStatus();

        Task task = new Task<Void>() {
            @Override public Void call() {
                // TODO : insert implementation with startRecording() here
                // horizontalOffset
                // verticalOffset
                // depthOffset
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            RootLayoutController.this.setRunning(false);
            RootLayoutController.this.handleRunningStatus();
        });
        new Thread(task).start();
    }

    private void handleRunningStatus() {
        if (!isRunning) {
            progressIndicator.setVisible(false);
            runButton.setText("Démarrer mesures");
        } else {
            progressIndicator.setVisible(true);
            runButton.setText("Stopper traitement");
        }
    }

    /*

    Task task = new Task<Void>() {
    @Override public Void call() {
        static final int max = 1000000;
        for (int i=1; i<=max; i++) {
            if (isCancelled()) {
               break;
            }
            updateProgress(i, max);
        }
        return null;
    }
    };
    ProgressBar bar = new ProgressBar();
    bar.progressProperty().bind(task.progressProperty());
    new Thread(task).start();
     */
}
