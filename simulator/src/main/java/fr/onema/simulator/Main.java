package fr.onema.simulator;

import org.apache.commons.cli.*;

public class Main {

    private static final String GENERATION_ARGUMENT = "gener";
    private static final String GENERATION_ARGUMENT_SHORT = "g";
    private static final String RUN_ARGUMENT = "run";
    private static final String RUN_ARGUMENT_SHORT = "r";
    private static final String COMPARE_OPTION = "compare";
    private static final String COMPARE_OPTION_SHORT = "c";

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine command = parser.parse(initOptions(), args);

            action(command);
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            // TODO : log exception properly
        }
    }

    private static void action(CommandLine command) {
        if (command.hasOption(GENERATION_ARGUMENT)) {
            generationAction(command.getOptionValues(GENERATION_ARGUMENT));
            return;
        }

        if (command.hasOption(RUN_ARGUMENT)) {
            String[] leftOverArgs = command.getArgs();
            if (leftOverArgs.length == 1) {
                runAction(command.getOptionValues(RUN_ARGUMENT), leftOverArgs[0]);
            } else {
                runAction(command.getOptionValues(RUN_ARGUMENT), null);
            }
            return;
        }

        if (command.hasOption(COMPARE_OPTION)) {
            compareAction(command.getOptionValues(COMPARE_OPTION));
        }

    }

    private static void compareAction(String[] optionValues) {
        String mergedFilePath = optionValues[0];
        String propertiesFilePath = optionValues[1];
        String resultFilePath = optionValues[2];

        System.out.println("Comparaison avec" +
                " Merged=" + mergedFilePath +
                " Properties=" + propertiesFilePath +
                " Result=" + resultFilePath
        );

        // TODO : Add comparaison call
    }

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

    private static void generationAction(String[] values) {
        String referenceFilePath = values[0];
        String virtualizedFilePath = values[1];
        String mergedFilePath = values[2];

        System.out.println("Generation avec" +
                " Ref=" + referenceFilePath +
                " Virtu=" + virtualizedFilePath +
                " Merged=" + mergedFilePath
        );

        // TODO : Add generation call
    }

    private static Options initOptions() {
        Option generatorOption = Option.builder(GENERATION_ARGUMENT_SHORT)
                .longOpt(GENERATION_ARGUMENT)
                .argName(GENERATION_ARGUMENT)
                .desc("Permet de generer les fichiers pour la virtualisation du flux MAVLink en provenance du Drone " +
                        "et le fichier de comparaison des positions calculees"
                )
                .numberOfArgs(3)
                .build();

        Option runOption = Option.builder(RUN_ARGUMENT_SHORT)
                .longOpt(RUN_ARGUMENT)
                .argName(RUN_ARGUMENT)
                .desc("Permet d'executer une simulation avec un fichier de virtualisation. L'hote/IP est optionnel " +
                        "(valeur par defaut : localhost)"
                )
                .numberOfArgs(1)
                .build();

        Option compareOption = Option.builder(COMPARE_OPTION_SHORT)
                .longOpt(COMPARE_OPTION)
                .argName(COMPARE_OPTION)
                .desc("Permet de comparer les positions calculees par l'algorithme de positionnement et les positions " +
                        "esperees.")
                .numberOfArgs(3)
                .build();

        return new Options()
                .addOption(generatorOption)
                .addOption(runOption)
                .addOption(compareOption);
    }
}
