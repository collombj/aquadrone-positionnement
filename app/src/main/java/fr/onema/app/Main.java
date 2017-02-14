package fr.onema.app;

import fr.onema.app.view.RootLayoutController;
import fr.onema.lib.tools.Configuration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/***
 * Classe Main de l'application graphique
 */
public class Main extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    /***
     * Méthode start appelée lors de l'initialisation de l'application pour définir les paramètres du conteneur de base
     * @param primaryStage Le conteneur principal
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("App");
        this.primaryStage.resizableProperty().set(false);
        initRootLayout(primaryStage);
    }

    private void initRootLayout(Stage primaryStage) throws java.io.IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/RootLayout.fxml"));
        fxmlLoader.setController(new RootLayoutController(Configuration.build("app/settings.properties")));
        fxmlLoader.load();
        Scene rootScene = new Scene(fxmlLoader.getRoot());
        primaryStage.setScene(rootScene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
