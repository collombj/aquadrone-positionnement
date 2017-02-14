package fr.onema.simulator;

import fr.onema.lib.file.FileManager;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Point d'entrée du Simulateur
 * <p>
 * Le main prend en charge le parsing de l'entrée en ligne de commande. Les explications relatifs à l'utilisation sont
 * disponibles via la commande "--help"
 *
 * @author Jeremie COLLOMB <contact@collombj.com>
 * @since 1.0
 */
public class Main {

    /**
     * Argument long pour la génération
     */
    private static final String GENERATION_ARGUMENT = "gener";
    /**
     * Argument court pour la génération
     */
    private static final String GENERATION_ARGUMENT_SHORT = "g";

    /**
     * Argument long pour l'exécution du simulateur
     */
    private static final String RUN_ARGUMENT = "run";
    /**
     * Argument court pour l'exécution du simulateur
     */
    private static final String RUN_ARGUMENT_SHORT = "r";

    /**
     * Argument long pour la comparaison
     */
    private static final String COMPARE_OPTION = "compare";
    /**
     * Argument court pour la comparaison
     */
    private static final String COMPARE_OPTION_SHORT = "c";

    /**
     * Nom de l'éxécutable généré (alias la commande à exécuter)
     */
    private static final String JAR_NAME = "simulator";

    private static final String LONG_ARGUMENT_SIGN = "\t" + JAR_NAME + " --";

    /**
     * Exemple d'utilisation de l'application avec différents paramètres
     */
    private static final String USAGE = LONG_ARGUMENT_SIGN + GENERATION_ARGUMENT + " reference.csv simulation.csv" +
            LONG_ARGUMENT_SIGN + RUN_ARGUMENT + " simulation.csv [hostname]" +
            LONG_ARGUMENT_SIGN + COMPARE_OPTION + " fusion.csv configuration.properties resultat.csv\n\n\n";

    private static final int NUMBER_OF_ARGS_GENERATION = 2;
    private static final int NUMBER_OF_ARGS_RUN = 1;
    private static final int NUMBER_OF_ARGS_COMPARE = 3;

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
     *
     * @param command paramètres saisie
     * @param options liste des options à utiliser pour les paramètres saisies
     */
    public static void action(CommandLine command, Options options) {
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
        if (command.hasOption(COMPARE_OPTION)) {
            compareAction(command.getOptionValues(COMPARE_OPTION));
            return;
        }

        // Par défaut on affiche l'interface d'aide
        printHelp(options);
    }

    /**
     * Exécution de l'action COMPARAISON
     *
     * @param values paramètres pour l'option
     */
    private static void compareAction(String[] values) {
        String mergedFilePath = values[0];
        String propertiesFilePath = values[1];
        String resultFilePath = values[2];

        System.out.println("Comparaison avec" +
                " Merged=" + mergedFilePath +
                " Properties=" + propertiesFilePath +
                " Result=" + resultFilePath
        );

        // TODO : Add comparaison call
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
            host = "localhost";
        }

        System.out.println("Virtualisation avec" +
                " Virtu=" + virtualizedFilePath +
                " Host=" + host
        );

        // TODO : Add virtualized run call
    }

    /**
     * Exécution de l'action GENERATION
     *
     * @param values paramètres pour l'option
     */
    private static void generationAction(String[] values) {
        String referenceFilePath = values[0];
        String virtualizedFilePath = values[1];

        System.out.println("Generation avec" +
                " Ref=" + referenceFilePath +
                " Virtu=" + virtualizedFilePath
        );
        try {
            Generator g = new Generator(referenceFilePath, virtualizedFilePath);
            g.convert();
        } catch (IOException e) {
            // TODO : handle exception
            // FileManager.LOGGER.log(Level.SEVERE, "Problème concernant les fichiers d'entrées");
        }
    }

    /**
     * Initialisation des options pour l'interface ligne de commande
     *
     * @return Options instanciées
     */
    public static Options initOptions() {
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

        Option compareOption = Option.builder(COMPARE_OPTION_SHORT)
                .longOpt(COMPARE_OPTION)
                .argName(COMPARE_OPTION)
                .desc("Permet de comparer les positions calculees par l'algorithme de positionnement et les positions " +
                        "esperees.")
                .numberOfArgs(NUMBER_OF_ARGS_COMPARE)
                .build();

        return new Options()
                .addOption(generatorOption)
                .addOption(runOption)
                .addOption(compareOption);
    }
}
