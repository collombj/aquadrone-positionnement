package fr.onema.app.view;

import fr.onema.app.model.CheckDependenciesAvailabilityTask;
import fr.onema.lib.tools.Configuration;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    private final Configuration c;
    private int diveDurationTolerance = 30;
    private volatile BooleanProperty isRunning = new SimpleBooleanProperty();
    private StringProperty startButtonLabel = new SimpleStringProperty();
    private Thread diveProgress = new Thread();
    private Thread dive = new Thread();

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

    @FXML
    private ProgressBar durationProgressBar;

    @FXML
    private ProgressBar precisionProgressBar;

    /***
     * Constructeur du controlleur du RootLayout
     * @param c Fichier de configuration comprenant les infos de la base Postgres
     */
    public RootLayoutController(Configuration c) {
        this.c = Objects.requireNonNull(c);
    }

    /***
     * Getter de la configuration associée à la vue Root de l'application
     * @return Configuration du RootLayout
     */
    public Configuration getConfiguration() {
        return c;
    }

    /***
     * Setter de la valeur de l'offset horizontal spécifier dans les paramètres
     * @param horizontalOffset La valeur de l'offset horizontal
     */
    public void setHorizontalOffset(double horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
    }

    /***
     * Setter de la valeur de l'offset vertical spécifier dans les paramètres
     * @param verticalOffset La valeur de l'offset vertical
     */
    public void setVerticalOffset(double verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    /***
     * Setter de la valeur de l'offset de profondeur spécifier dans les paramètres
     * @param depthOffset La valeur de l'offset de profondeur
     */
    public void setDepthOffset(double depthOffset) {
        this.depthOffset = depthOffset;
    }

    /***
     * Setter de la valeur d'état de la plongée
     * @param running La valeur de l'état de plongée
     */
    private void setRunning(boolean running) {
        isRunning.setValue(running);
    }

    /***
     * Méthode permettant de déclarer le scheduler de tâches pour interroger l'état des dépendances
     */
    @FXML
    private void initialize() {
        progressIndicator.setVisible(false);
        runButton.textProperty().bindBidirectional(startButtonLabel);
        startButtonLabel.setValue("Démarrer mesures");
        isRunning.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                handleRunningStatus();
            });
        });
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new CheckDependenciesAvailabilityTask(this), 0, 30_000);
    }

    /***
     * Permet de mettre à jour la couleur du Label d'état de la connexion Mavlink
     * @param c La couleur à appliquer
     */
    @FXML
    public void updateDatabaseColor(Color c) {
        dbLabel.setTextFill(c);
    }

    /***
     * Permet de mettre à jour la couleur du Label d'état de la connexion Mavlink
     * @param c La couleur à appliquer
     */
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

    /***
     * Méthode exécutée par le bouton Start / Stop plongée
     */
    @FXML
    private void executeMeasures() {
        if (isRunning.get()) {
            dive.interrupt();
            diveProgress.interrupt();
            setRunning(false);
        } else {
            setupDiveProgressThread();
            setupDiveThread();
            setRunning(true);
            diveProgress.start();
            dive.start();
        }
    }

    /***
     * Permet de configurer la thread assurant l'opération de plongée
     */
    private void setupDiveThread() {
        Task diveTask = new Task() {
            @Override
            protected Void call() throws Exception {
                try {
                    // TODO : insert implementation with startRecording() here
                    Thread.sleep(3000);
                    System.out.println("Task completed with followings parameters : \n"
                            + "Horizontal Offset = " + horizontalOffset + "\n"
                            + "Vertical Offset = " + verticalOffset + "\n"
                            + "Depth Offset = " + depthOffset + "\n"
                            + "Duration Tolerance = " + diveDurationTolerance + "\n"
                            + "Precision = NOT IMPLEMENTED YET"
                    );
                } catch (InterruptedException e) {
                    // ignore
                } finally {
                    setRunning(false);
                    Thread.currentThread().interrupt();
                }
                return null;
            }
        };
        dive = new Thread(diveTask);
    }

    /***
     * Permet de configurer la thread assurant le graphique de la barre de progression de plongée
     */
    private void setupDiveProgressThread() {
        Task diveProgressTask = new Task() {
            @Override public Void call() {
                final double max = RootLayoutController.this.diveDurationTolerance * 5;
                for (double i = 0; i <= max; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    updateProgress(i, max);
                }
                return null;
            }
        };
        durationProgressBar.progressProperty().bind(diveProgressTask.progressProperty());
        diveProgress = new Thread(diveProgressTask);
    }

    /***
     * Met à jour les éléments de la vue en fonction du status de l'opération de plongée
     */
    private void handleRunningStatus() {
        if (!isRunning.getValue()) {
            durationProgressBar.progressProperty().unbind();
            durationProgressBar.setProgress(0.0);
            progressIndicator.setVisible(false);
            startButtonLabel.setValue("Démarrer mesures");
        } else {
            progressIndicator.setVisible(true);
            startButtonLabel.setValue("Stopper traitement");
        }
    }
}
