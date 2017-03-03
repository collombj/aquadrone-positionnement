package fr.onema.app.view;

import fr.onema.app.Main;
import fr.onema.app.model.CheckDependenciesAvailabilityTask;
import fr.onema.lib.drone.Dive;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.worker.MessageWorker;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/***
 * Controlleur associé à la vue RootLayout.fxml
 */
public class RootLayoutController {
    private final Main main;
    private double offsetX = 0;
    private double offsetY = 0;
    private double offsetZ = 0;
    private volatile BooleanProperty isRunning = new SimpleBooleanProperty();
    private StringProperty startButtonLabel = new SimpleStringProperty();
    private Thread precisionProgress = new Thread();
    private Thread diveProgress = new Thread();
    private Thread dive = new Thread();
    private List<TableSensor> sensors = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#.##");
    private boolean configFileUsed;

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

    @FXML
    private TableView<TableSensor> sensorsTableView;

    @FXML
    private TableColumn<TableSensor, String> type;

    @FXML
    private TableColumn<TableSensor, String> state;

    /***
     * Constructeur du controlleur du RootLayout
     */
    public RootLayoutController(Main main) {
        this.main = Objects.requireNonNull(main);
    }

    /***
     * Setter de la valeur de l'offset horizontal spécifier dans les paramètres
     * @param offsetX La valeur de l'offset horizontal
     */
    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    /***
     * Setter de la valeur de l'offset vertical spécifier dans les paramètres
     * @param offsetY La valeur de l'offset vertical
     */
    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    /***
     * Setter de la valeur de l'offset de profondeur spécifier dans les paramètres
     * @param offsetZ La valeur de l'offset de profondeur
     */
    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    /***
     * Setter de la valeur d'état de la plongée
     * @param running La valeur de l'état de plongée
     */
    public void setRunning(boolean running) {
        isRunning.setValue(running);
    }

    /***
     * Méthode permettant de déclarer le scheduler de tâches pour interroger l'état des dépendances
     */
    @FXML
    private void initialize() {
        progressIndicator.setVisible(false);
        precisionProgressBar.setProgress(1.0);
        runButton.textProperty().bindBidirectional(startButtonLabel);
        startButtonLabel.setValue("Démarrer mesures");
        isRunning.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                handleRunningStatus();
            });
        });

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new CheckDependenciesAvailabilityTask(main), 0, 1_000);
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
            stage.resizableProperty().setValue(false);
            ConfigurationController controller = loader.getController();
            controller.initialize();
            if (!configFileUsed) {
                controller.insertSpinnerValues(main.getConfiguration().getOffset().getAccelerationOffsetX(), main.getConfiguration().getOffset().getAccelerationOffsetY(), main.getConfiguration().getOffset().getAccelerationOffsetZ());
                configFileUsed = true;
            } else {
                controller.insertSpinnerValues(offsetX, offsetY, offsetZ);
            }
            controller.init(this, main);
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
            precisionProgress.interrupt();
            main.stopExecution();
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
                main.execute();
                Thread.currentThread().interrupt();
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
            @Override
            public Void call() {
                final double max = main.getConfiguration().getDiveData().getDureemax() * 5;
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
            precisionProgressBar.setProgress(1.0);
            progressIndicator.setVisible(false);
            startButtonLabel.setValue("Démarrer mesures");
        } else {
            progressIndicator.setVisible(true);
            startButtonLabel.setValue("Stopper traitement");
        }
    }

    public void updatePrecisionProgress(MessageWorker worker, Configuration configuration) {
        Dive currentDive = worker.getDive();
        if (currentDive != null) {
            int numberOfMovements = currentDive.getNumberOfMovement();
            double maxMovements = configuration.getDiveData().getMouvementsmax();
            Platform.runLater(() -> precisionProgressBar.setProgress(numberOfMovements / maxMovements));
        } else {
            Platform.runLater(() -> precisionProgressBar.setProgress(1.0));
        }
    }

    public void updateSensors(Map<String, Long> map) {
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        state.setCellValueFactory(new PropertyValueFactory<>("state"));
        sensors.clear();

        for (Map.Entry<String, Long> e : map.entrySet()) {
            sensors.add(new TableSensor(e.getKey(), checkStateTime(e.getValue())));
        }

        sensorsTableView.getItems().setAll(sensors);
        sensorsTableView.setFixedCellSize(25);
        sensorsTableView.prefHeightProperty().bind(sensorsTableView.fixedCellSizeProperty().multiply(Bindings.size(sensorsTableView.getItems()).add(1.01)));
        sensorsTableView.minHeightProperty().bind(sensorsTableView.prefHeightProperty());
        sensorsTableView.maxHeightProperty().bind(sensorsTableView.prefHeightProperty());
        sensorsTableView.refresh();
        Platform.runLater(() -> resizeParent());
    }

    private String checkStateTime(long value) {
        long current = System.currentTimeMillis();
        double diffSeconds = (current - value) / 1000.0;
        if (diffSeconds > main.getConfiguration().getDiveData().getDelaicapteurhs()) {
            return "inactif depuis : " + df.format(diffSeconds) + " s";
        } else {
            return "actif (dernière activité : " + df.format(diffSeconds) + " s)";
        }
    }

    /***
     *
     */
    public class TableSensor {
        private final String type;
        private final String state;

        private TableSensor(String type, String state) {
            this.type = Objects.requireNonNull(type);
            this.state = Objects.requireNonNull(state);
        }

        public String getType() {
            return type;
        }

        public String getState() {
            return state;
        }
    }
}
