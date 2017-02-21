package fr.onema.app;

import fr.onema.app.view.RootLayoutController;
import fr.onema.lib.network.ServerListener;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.worker.DatabaseWorker;
import fr.onema.lib.worker.MessageWorker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

/***
 * Classe Main de l'application graphique
 */
public class Main extends Application {
    public static final double HORIZONTAL_DEFAULT_VALUE = 0;
    public static final double VERTICAL_DEFAULT_VALUE = 0;
    public static final double DEPTH_DEFAULT_VALUE = 0;
    private Stage parent;
    private ServerListener server;
    private Configuration configuration;
    private DatabaseWorker databaseWorker;
    private MessageWorker messageWorker;
    private RootLayoutController rlc;
    // TODO : replace with customized logging system
    private Logger logger;

    public static void main(String[] args) throws FileNotFoundException {
        launch(args);
    }

    public RootLayoutController getRlc() {
        return rlc;
    }

    /***
     * Méthode start appelée lors de l'initialisation de l'application pour définir les paramètres du conteneur de base
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.configuration = Configuration.getInstance();
        this.databaseWorker = DatabaseWorker.getInstance();
        this.databaseWorker.start();
        this.server = new ServerListener(14550);
        this.server.start();
        this.messageWorker = server.getMessageWorker();
        this.parent = primaryStage;
        this.parent.setTitle("App");
        this.parent.resizableProperty().set(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/RootLayout.fxml"));
        rlc = new RootLayoutController(this);
        fxmlLoader.setController(rlc);
        fxmlLoader.load();
        primaryStage.setScene(new Scene(fxmlLoader.getRoot()));
        primaryStage.sizeToScene();
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Stop toutes les threads lorsque l'on quitte l'application
        server.stop();
        databaseWorker.stop();
    }

    public void execute() {
        messageWorker.startRecording();

        System.out.println("Task completed with followings parameters : \n"
                + "Horizontal Offset = " + configuration.getFlow().getLat() + "\n"
                + "Vertical Offset = " + configuration.getFlow().getLon() + "\n"
                + "Depth Offset = " + configuration.getFlow().getAlt() + "\n"
                + "Duration Tolerance = " + configuration.getDiveData().getDureemax() + "\n"
                + "Precision = " + configuration.getDiveData().getPrecision());
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public MessageWorker getMessageWorker() {
        return messageWorker;
    }

    public Stage getParent() {
        return parent;
    }

    public void stopExecution() {
        messageWorker.stopRecording();
    }
}
