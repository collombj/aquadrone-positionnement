package fr.onema.simulator;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.tools.Configuration;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Point d'entrée du Simulateur
 * Le main prend en charge le parsing de l'entrée en ligne de commande. Les explications relatifs à l'utilisation sont
 * disponibles via la commande "--help"
 */
public class Main {

    /**
     * Nombre d'argument pour la génération
     */
    private static final int NUMBER_OF_ARGS_GENERATION = 2;

    /**
     * Argument long pour la génération
     */
    private static final String GENERATION_ARGUMENT = "gener";

    /**
     * Argument court pour la génération
     */
    private static final String GENERATION_ARGUMENT_SHORT = "g";

    /**
     * Nombre d'argument pour l'exécution
     */
    private static final int NUMBER_OF_ARGS_RUN = 1;

    /**
     * Argument long pour l'exécution du simulateur
     */
    private static final String RUN_ARGUMENT = "run";

    /**
     * Argument court pour l'exécution du simulateur
     */
    private static final String RUN_ARGUMENT_SHORT = "r";

    /**
     * Nombre d'argument pour la comparaison
     */
    private static final int NUMBER_OF_ARGS_COMPARE = 2;

    /**
     * Argument long pour la comparaison
     */
    private static final String COMPARE_ARGUMENT = "compare";

    /**
     * Argument court pour la comparaison
     */
    private static final String COMPARE_ARGUMENT_SHORT = "c";

    /**
     * Nombre d'argument pour la préparation des fichiers
     */
    private static final int NUMBER_OF_ARGS_PREPARE = 2;

    /**
     * Argument long pour la préparation
     */
    private static final String PREPARE_ARGUMENT = "prepare";

    /**
     * Argument court pour la préparation
     */
    private static final String PREPARE_ARGUMENT_SHORT = "p";

    /**
     * Nom de l'éxécutable généré (alias la commande à exécuter)
     */
    private static final String JAR_NAME = "simulator";

    /**
     * Nom d'hôte par défaut de la cible de la simulation
     */
    private static final String HOST = "localhost";

    /**
     * port par défaut de la cible de la simulation
     */
    private static final int PORT = 14550;

    /**
     * Nombre d'envoie par seconde pour la simulation
     */
    private static final int SPEED = 4;

    private static final String LONG_ARGUMENT_SIGN = "\t" + JAR_NAME + " --";

    /**
     * Exemple d'utilisation de l'application avec différents paramètres
     */
    private static final String USAGE = LONG_ARGUMENT_SIGN + GENERATION_ARGUMENT + " reference.csv simulation.csv" +
            LONG_ARGUMENT_SIGN + RUN_ARGUMENT + " simulation.csv [hostname]" +
            LONG_ARGUMENT_SIGN + COMPARE_ARGUMENT + " fusion.csv resultat.csv [configuration.properties]\n\n\n";

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Main() {
        // Avoid instantiation
    }

    /**
     * Methode static représentant le point d'entrée dans l'application.
     *
     * @param args Arguments passés au moment de l'exécution de l'application (le simulateur)
     */
    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = initOptions();

        try {
            CommandLine command = parser.parse(options, args);
            action(command, options);
        } catch (ParseException e) {
            printHelp(options);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Affichage de l'aide sur [System.out]
     *
     * @param options liste des options à afficher
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(JAR_NAME, USAGE, options, "", false);
    }

    /**
     * Filtre et exécute les actions relativent au paramètres passés en paramètres
     */
    private static void action(CommandLine command, Options options) {
        // Génération des CSVs pour l'utilisation du simulateur
        if (command.hasOption(GENERATION_ARGUMENT)) {
            generationAction(command.getOptionValues(GENERATION_ARGUMENT));
            return;
        }

        // Exécution de la simulation
        if (command.hasOption(RUN_ARGUMENT)) {
            String[] leftOverArgs = command.getArgs();
            if (leftOverArgs.length == 1) {
                runAction(command.getOptionValues(RUN_ARGUMENT), leftOverArgs[0]);  // L'IP/hôte cible est optionnel
            } else {
                runAction(command.getOptionValues(RUN_ARGUMENT), null);
            }
            return;
        }

        // Comparaison des positions calculées et espérées
        if (command.hasOption(COMPARE_ARGUMENT)) {
            String[] leftOverArgs = command.getArgs();
            if (leftOverArgs.length == 1) {
                compareAction(command.getOptionValues(COMPARE_ARGUMENT), leftOverArgs[0]);  // Le fichier de configuration
            } else {
                compareAction(command.getOptionValues(COMPARE_ARGUMENT), null);
            }
            return;
        }
        // Comparaison des positions calculées et espérées
        if (command.hasOption(PREPARE_ARGUMENT)) {
            prepareAction(command.getOptionValues(PREPARE_ARGUMENT));
            return;
        }

        // Par défaut on affiche l'interface d'aide
        printHelp(options);
    }

    private static void prepareAction(String[] values) {
        String input = values[0];
        String output = values[1];

        try {
            MissingPointsGenerator missingPoints = MissingPointsGenerator.build(input);
            missingPoints.generateOutput(output);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Exécution de l'action COMPARAISON
     *
     * @param values paramètres pour l'option
     */
    private static void compareAction(String[] values, String settings) {
        String referenceFilePath = values[0];
        String resultFilePath = values[1];
        String propertiesFilePath = settings;

        if (propertiesFilePath == null) {
            propertiesFilePath = "settings.properties";
        }

        FileManager fileManager = new FileManager(referenceFilePath, "",
                resultFilePath);
        Virtualizer virtualizer = new Virtualizer(fileManager, 4, "", HOST, 14550);

        try {
            Configuration configuration = Configuration.build(propertiesFilePath);
            virtualizer.compare(configuration, 0.3);
            String list = fileManager.getResults("\t").stream().reduce("", (a, b) -> a + "\n" + b);
            LOGGER.log(Level.INFO, list);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Unable to load the properties file", e);
        } catch (ComparisonException e) {
            LOGGER.log(Level.SEVERE, "Error during the comparison", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read in the result file", e);
        }
    }

    /**
     * Exécution de l'action RUN -- simulation
     *
     * @param values    paramètres pour l'option
     * @param hostParam Hostname saisie en paramètre (optionnel - peut être null)
     */
    private static void runAction(String[] values, String hostParam) {
        String virtualizedFilePath = values[0];

        String host = hostParam;
        if (host == null) {
            host = HOST;
        }

        FileManager fileManager = new FileManager("", virtualizedFilePath,
                "");
        Virtualizer virtualizer = new Virtualizer(fileManager, SPEED, "", host, PORT);

        try {
            LOGGER.log(Level.INFO, "Sending in progress");
            virtualizer.start();
            LOGGER.log(Level.INFO, "Sending is over");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during the simulation", e);
        }
    }

    /**
     * Exécution de l'action GENERATION
     *
     * @param values paramètres pour l'option
     */
    private static void generationAction(String[] values) {
        String referenceFilePath = values[0];
        String virtualizedFilePath = values[1];

        try {
            Generator g = new Generator(referenceFilePath, virtualizedFilePath);
            g.convert();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Problem with the conversion in the generator", e);
        }
    }

    /**
     * Initialisation des options pour l'interface ligne de commande
     *
     * @return Options instanciées
     */
    private static Options initOptions() {
        Option generatorOption = Option.builder(GENERATION_ARGUMENT_SHORT)
                .longOpt(GENERATION_ARGUMENT)
                .argName(GENERATION_ARGUMENT)
                .desc("Permet de generer les fichiers pour la virtualisation du flux MAVLink en provenance du Drone " +
                        "et le fichier de comparaison des positions calculees"
                )
                .numberOfArgs(NUMBER_OF_ARGS_GENERATION)
                .build();

        Option runOption = Option.builder(RUN_ARGUMENT_SHORT)
                .longOpt(RUN_ARGUMENT)
                .argName(RUN_ARGUMENT)
                .desc("Permet d'executer une simulation avec un fichier de virtualisation. L'hote/IP est optionnel " +
                        "(valeur par defaut : localhost)"
                )
                .numberOfArgs(NUMBER_OF_ARGS_RUN)
                .build();

        Option compareOption = Option.builder(COMPARE_ARGUMENT_SHORT)
                .longOpt(COMPARE_ARGUMENT)
                .argName(COMPARE_ARGUMENT)
                .desc("Permet de comparer les positions calculees par l'algorithme de positionnement et les positions " +
                        "esperees. Le fichier de configuration est optionnel (valeur par défaut : settings.properties")
                .numberOfArgs(NUMBER_OF_ARGS_COMPARE)
                .build();

        Option prepareOption = Option.builder(PREPARE_ARGUMENT_SHORT)
                .longOpt(PREPARE_ARGUMENT)
                .argName(PREPARE_ARGUMENT)
                .desc("Permet de générer un fichier de reference avec des points tous les 50 cms (maximum)." +
                        "Le fichier est egalement formate:\n" +
                        "\t- Latitude et Longitude sont multiplies par 10^7\n" +
                        "\t- Altitude est multiplie par 10^3")
                .numberOfArgs(NUMBER_OF_ARGS_PREPARE)
                .build();

        return new Options()
                .addOption(generatorOption)
                .addOption(runOption)
                .addOption(compareOption)
                .addOption(prepareOption);
    }
}
