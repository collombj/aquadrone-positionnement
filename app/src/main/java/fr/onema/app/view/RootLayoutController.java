package fr.onema.app.view;

import fr.onema.app.Main;
import fr.onema.app.model.CheckDependenciesAvailabilityTask;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
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
import java.util.*;

/***
 * Controlleur associé à la vue RootLayout.fxml
 */
public class RootLayoutController {
    private final Main main;
    private double horizontalOffset = 0;
    private double verticalOffset = 0;
    private double depthOffset = 0;

    // TODO : gestion des paramètres via le fichier de configuration
    private int diveDurationTolerance = 120;
    private int precision = 50;

    private volatile BooleanProperty isRunning = new SimpleBooleanProperty();
    private StringProperty startButtonLabel = new SimpleStringProperty();
    private Thread diveProgress = new Thread();
    private Thread dive = new Thread();
    private List<TableSensor> sensors = new ArrayList<>();

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
     */
    public RootLayoutController(Main main) {
        this.main = Objects.requireNonNull(main);
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
        timer.scheduleAtFixedRate(new CheckDependenciesAvailabilityTask(this, main), 0, 10_000);
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
                try {
                    // TODO : remove
                    Thread.sleep(60000);
                    //
                    main.execute(horizontalOffset, verticalOffset, depthOffset, diveDurationTolerance, precision);
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

    @FXML
    private TableView<TableSensor> sensorsTableView;

    @FXML
    private TableColumn<TableSensor, Integer> id;

    @FXML
    private TableColumn<TableSensor, String> type;

    @FXML
    private TableColumn<TableSensor, Long> state;

    /***
     *
     */
    public class TableSensor {
        private final int id;
        private final String type;
        private final long state;

        public TableSensor(int id, String type, long state) {
            this.id = id;
            this.type = Objects.requireNonNull(type);
            this.state = state;
        }

        public int getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public long getState() {
            return state;
        }
    }

    public void updateSensors(Map<String, Long> map) {
        id.setCellValueFactory(new PropertyValueFactory<TableSensor, Integer>("id"));
        type.setCellValueFactory(new PropertyValueFactory<TableSensor, String>("type"));
        state.setCellValueFactory(new PropertyValueFactory<TableSensor, Long>("state"));
        // TODO : handle ID
        sensors.clear();
        map.entrySet().forEach(e -> sensors.add(new TableSensor(0, e.getKey(), e.getValue())));
        sensorsTableView.getItems().setAll(sensors);
        sensorsTableView.refresh();
    }
}
