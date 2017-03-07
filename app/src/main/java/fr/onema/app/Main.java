package fr.onema.app;

import fr.onema.app.view.RootLayoutController;
import fr.onema.lib.file.manager.VirtualizedOutput;
import fr.onema.lib.network.ServerListener;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.worker.DatabaseWorker;
import fr.onema.lib.worker.MessageWorker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.cli.*;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/***
 * Classe Main de l'application graphique
 */
public class Main extends Application {
    public static final double X_DEFAULT_VALUE = 0;
    public static final double Y_DEFAULT_VALUE = 0;
    public static final double Z_DEFAULT_VALUE = 0;
    private static final String DEBUG_ARGUMENT_SHORT = "d";
    private static final String DEBUG_ARGUMENT = "debug";
    private static final int NUMBER_OF_ARGS_DEBUG = 1;
    private static final String JAR_NAME = "simulator";
    private static final String USAGE = "\t" + JAR_NAME +
            "\t" + JAR_NAME + " --" + DEBUG_ARGUMENT + " log.csv" +
            "\n\n\n";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());
    private static final String SETTINGS_ARGUMENT_SHORT = "D";
    private static final String PORT_PROPERTIES = "port";
    private static String PORT = "14550";
    private static String LOG_FILE = null;
    private Stage parent;
    private ServerListener server;
    private Configuration configuration;
    private DatabaseWorker databaseWorker;
    private MessageWorker messageWorker;
    private RootLayoutController rlc;

    /***
     * Constructeur du main (racine de l'application graphique)
     * @param args Les arguments passés au démarrage de l'application
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("settings.properties");
        CommandLineParser parser = new DefaultParser();
        Options options = initOptions();

        try {
            CommandLine command = parser.parse(options, args);
            action(command);
            launch(args);
        } catch (ParseException e) {
            printHelp(options);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        } finally {
            System.exit(0);
        }
    }

    /**
     * Initialisation des options pour l'interface ligne de commande
     * @return Options instanciées
     */
    private static Options initOptions() {
        Option debugOption = Option.builder(DEBUG_ARGUMENT_SHORT)
                .longOpt(DEBUG_ARGUMENT)
                .argName(DEBUG_ARGUMENT)
                .desc("Permet de generer un fichier de debug representant les messages MAVLink. Le fichier de sortie est" +
                        "au format \"Simulation\".")
                .numberOfArgs(NUMBER_OF_ARGS_DEBUG)
                .build();

        Option settings = Option.builder(SETTINGS_ARGUMENT_SHORT)
                .numberOfArgs(2)
                .valueSeparator('=')
                .desc("Parametrage de l'application\n" +
                        "Parametre possible :\n" +
                        "\t - " + PORT_PROPERTIES + " : port sur lequel recevoir les messages MAVLink\n")
                .build();

        return new Options()
                .addOption(debugOption)
                .addOption(settings);
    }

    /**
     * Affichage de l'aide sur [System.out]
     * @param options liste des options à afficher
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(JAR_NAME, USAGE, options, "", false);
    }

    /**
     * Filtre et exécute les actions relativent au paramètres passés en paramètres
     */
    private static void action(CommandLine command) {
        if (command.hasOption(DEBUG_ARGUMENT_SHORT)) {
            LOG_FILE = command.getOptionValues(DEBUG_ARGUMENT_SHORT)[0];
        }

        if (command.hasOption(SETTINGS_ARGUMENT_SHORT)) {
            PORT = command.getOptionProperties(SETTINGS_ARGUMENT_SHORT).getProperty(PORT_PROPERTIES, PORT);
        }
    }

    /***
     * Méthode start appelée lors de l'initialisation de l'application pour définir les paramètres du conteneur de base
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            initWorker();

            this.parent = primaryStage;
            this.parent.setTitle("SIREN");
            this.parent.resizableProperty().set(false);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/RootLayout.fxml"));
            rlc = new RootLayoutController(this);
            fxmlLoader.setController(rlc);
            fxmlLoader.load();
            primaryStage.setScene(new Scene(fxmlLoader.getRoot()));
            primaryStage.sizeToScene();
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private void initWorker() throws FileNotFoundException {
        this.configuration = Configuration.getInstance();
        this.databaseWorker = DatabaseWorker.getInstance();
        if (this.databaseWorker == null) {
            return;
        }
        this.databaseWorker.start();
        this.server = new ServerListener(Integer.parseInt(PORT));
        this.server.start();
        this.messageWorker = server.getMessageWorker();
        if (LOG_FILE != null) {
            this.messageWorker.setTracer(new VirtualizedOutput(LOG_FILE));
            this.messageWorker.startLogger();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.stop();
        databaseWorker.stop();
    }

    /***
     * Méthode permettant d'appliquer un flag aux messages traités pour analyses futures
     */
    public void execute() {
        rlc.setRunning(true);
        messageWorker.startRecording();
    }

    /***
     * Methode permettant de retirer le flag assigné aux messages traités pour analyses futures
     */
    public void stopExecution() {
        rlc.setRunning(false);
        messageWorker.stopRecording();
    }

    /***
     * Getter du Layout principal de l'application graphique
     * @return Le layout principal
     */
    public RootLayoutController getRlc() {
        return rlc;
    }

    /***
     * Getter de la configuration de l'application associée à l'exécution
     * @return La configuration de l'application
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /***
     * Getter du worker en charge du traitement des messages Mavlink
     * @return Le worker traitant le flux Mavlink
     */
    public MessageWorker getMessageWorker() {
        return messageWorker;
    }
}
