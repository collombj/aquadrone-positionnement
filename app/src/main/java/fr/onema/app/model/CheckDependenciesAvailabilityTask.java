package fr.onema.app.model;

import fr.onema.app.Main;
import fr.onema.lib.database.DatabaseDriver;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.worker.MessageWorker;
import javafx.scene.paint.Color;

import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

/***
 * Classe permettant la déclaration du scheduler chargé de requêter les dépendances
 */
public class CheckDependenciesAvailabilityTask extends TimerTask {
    private final Main main;

    public CheckDependenciesAvailabilityTask(Main main) {
        this.main = Objects.requireNonNull(main);
    }

    private static Map<String, Long> checkSensorsAvailability(MessageWorker worker) {
        return worker.getMeasuresStates();
    }

    /***
     * Permet de vérifier l'état de la base de donnée Postgres
     * @param c Le fichier de configuration contenant les informations de connexion
     * @return L'état de la base
     */
    public static boolean checkPostgresAvailability(Configuration c) {
        Objects.requireNonNull(c);
        try {
            DatabaseDriver dd = DatabaseDriver.build(c);
            dd.initAsReadable();
            dd.closeConnection();
        } catch (Exception e) {
            // FileManager.LOGGER.log(Level.FINE, e.getMessage());
            return false;
        }
        return true;
    }

    /***
     * Permet de vérifier l'état du flux Mavlink
     * @return L'état du flux
     */
    private static boolean checkMavlinkAvailability(MessageWorker worker, Configuration conf) {
        int mavlinkTimeoutInMs = conf.getDiveData().getFrequencetestmavlink() * 1_000;
        long diff = System.currentTimeMillis() - worker.getMavLinkConnection();
        return diff < mavlinkTimeoutInMs;
    }

    /***
     * Permet de mettre à jour la couleur des dépendances en fonction de leur état
     */
    @Override
    public void run() {
        if (checkPostgresAvailability(main.getConfiguration())) {
            main.getRlc().updateDatabaseColor(Color.GREEN);
        } else {
            main.getRlc().updateDatabaseColor(Color.RED);
        }

        if (checkMavlinkAvailability(main.getMessageWorker(), main.getConfiguration())) {
            main.getRlc().updateMavlinkColor(Color.GREEN);
        } else {
            main.getRlc().updateMavlinkColor(Color.RED);
        }
        main.getRlc().updateSensors(checkSensorsAvailability(main.getMessageWorker()));
        main.getRlc().updatePrecisionProgress(main.getMessageWorker(), main.getConfiguration());
    }
}
