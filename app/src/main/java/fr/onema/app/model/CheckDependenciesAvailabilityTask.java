package fr.onema.app.model;

import fr.onema.app.Main;
import fr.onema.app.view.RootLayoutController;
import fr.onema.lib.database.DatabaseDriver;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.worker.MessageWorker;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

/**
 * Created by you on 14/02/2017.
 */

/***
 * Classe permettant la déclaration du scheduler chargé de requêter les dépendances
 */
public class CheckDependenciesAvailabilityTask extends TimerTask {
    private final RootLayoutController rlc;
    private final Main main;

    public CheckDependenciesAvailabilityTask(RootLayoutController rlc, Main main) {
        this.rlc = Objects.requireNonNull(rlc);
        this.main = Objects.requireNonNull(main);
    }

    /***
     * Permet de mettre à jour la couleur des dépendances en fonction de leur état
     */
    @Override
    public void run() {
        if (checkPostgresAvailability(main.getConfiguration())) {
            rlc.updateDatabaseColor(Color.GREEN);
        } else {
            rlc.updateDatabaseColor(Color.RED);
        }

        if (checkMavlinkAvailability(main.getMessageWorker())) {
            rlc.updateMavlinkColor(Color.GREEN);
        } else {
            rlc.updateMavlinkColor(Color.RED);
        }

        /*
        checkSensorsAvailability(main.getMessageWorker()).entrySet().forEach(e -> {
            // clear tab
            // insert sensors states
        });
        */
        Map<String, Long> map = new HashMap<>();
        map.put("Balises GPS", (long) 69696969);
        map.put("Capteur pression", (long) 69696969);
        map.put("Sonde temperature", (long) 69696969);
        rlc.updateSensors(map);
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
    public static boolean checkMavlinkAvailability(MessageWorker worker) {
        return worker.getMavLinkConnection();
    }
}
