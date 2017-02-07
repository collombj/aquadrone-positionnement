package fr.onema.app;

import fr.onema.lib.TestLib;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("App");
        initRootLayout(primaryStage);
    }

    private void initRootLayout(Stage primaryStage) throws java.io.IOException {
        rootLayout = FXMLLoader.load(getClass().getResource("view/RootLayout.fxml"));
        primaryStage.setScene(new Scene(rootLayout, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
