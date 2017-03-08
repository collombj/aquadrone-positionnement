package fr.onema.lib.tools;

import org.apache.log4j.*;
import org.apache.log4j.varia.LevelRangeFilter;

import java.io.IOException;

/**
 * Class utilitaire pour intensier le gestionnaire de log
 */
public class LogSettings {
    /**
     * Méthode permettant d'initialiser le gestionnaire de Log
     * @throws IOException Si le fichier de log n'a pas pu être créé
     */
    public static void initLog4j() throws IOException {
        PatternLayout layoutConsole = new PatternLayout();
        layoutConsole.setConversionPattern("[%d{yyyy-MM-dd HH:mm:ss}] %5p %c{1} - %m%n");

        PatternLayout layoutFile = new PatternLayout();
        layoutFile.setConversionPattern("[%d{yyyy-MM-dd HH:mm:ss}] %5p %c{1}:%L - %m%n");

        LevelRangeFilter debug = new LevelRangeFilter();
        debug.setLevelMin(Level.DEBUG);
        debug.setLevelMax(Level.ERROR);

        LevelRangeFilter warn = new LevelRangeFilter();
        warn.setLevelMin(Level.INFO);
        warn.setLevelMax(Level.ERROR);

        RollingFileAppender file = new RollingFileAppender(layoutConsole, "logs.log", true);
        file.addFilter(debug);

        ConsoleAppender console = new ConsoleAppender(layoutConsole);
        console.addFilter(warn);

        BasicConfigurator.configure(console);
        BasicConfigurator.configure(file);
    }
}
