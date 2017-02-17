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

import java.util.logging.Logger;

/***
 * Classe Main de l'application graphique
 */
public class Main extends Application {
    public static final double HORIZONTAL_DEFAULT_VALUE = 0;
    public static final double VERTICAL_DEFAULT_VALUE = 0;
    public static final double DEPTH_DEFAULT_VALUE = 0;

    public Stage getParent() {
        return parent;
    }

    private Stage parent;
    private ServerListener server;
    private DatabaseWorker databaseWorker;
    private MessageWorker messageWorker;
    private Configuration configuration;

    // TODO : replace with customized logging system
    private Logger logger;

    public static void main(String[] args) {
        launch(args);
    }

    /***
     * Méthode start appelée lors de l'initialisation de l'application pour définir les paramètres du conteneur de base
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO : complete
        this.configuration = Configuration.build("settings.properties");
        this.server = new ServerListener(14550);
        this.databaseWorker = DatabaseWorker.getInstance();
        this.messageWorker = new MessageWorker();
        //

        this.parent = primaryStage;
        this.parent.setTitle("App");
        this.parent.resizableProperty().set(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/RootLayout.fxml"));
        RootLayoutController rlc = new RootLayoutController(this);
        fxmlLoader.setController(rlc);
        fxmlLoader.load();
        primaryStage.setScene(new Scene(fxmlLoader.getRoot()));
        primaryStage.sizeToScene();
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    public void execute(double horizontalOffset, double verticalOffset, double depthOffset, int diveDurationTolerance, double precision) {

        // TODO : implement UC 4 here

        System.out.println("Task completed with followings parameters : \n"
                + "Horizontal Offset = " + horizontalOffset + "\n"
                + "Vertical Offset = " + verticalOffset + "\n"
                + "Depth Offset = " + depthOffset + "\n"
                + "Duration Tolerance = " + diveDurationTolerance + "\n"
                + "Precision = " + precision);
    }

    public Configuration getConfiguration() {
        return configuration;
    }


    public MessageWorker getMessageWorker() {
        return messageWorker;
    }

    public void stopExecution() {
        // TODO : close everything here
    }
}
