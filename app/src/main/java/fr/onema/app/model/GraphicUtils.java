package fr.onema.app.model;

import fr.onema.lib.database.DatabaseDriver;
import fr.onema.lib.tools.Configuration;

import java.util.Objects;

/**
 * Created by you on 14/02/2017.
 */

/***
 * Classe permettant l'accès à des méthodes utilitaires propres à l'application graphique
 */
public class GraphicUtils {
    public static final double HORIZONTAL_DEFAULT_VALUE = 0;
    public static final double VERTICAL_DEFAULT_VALUE = 0;
    public static final double DEPTH_DEFAULT_VALUE = 0;

    private GraphicUtils() {
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
    public static boolean checkMavlinkAvailability() {
        // TODO : implement availability with MessageWorker
        return true;
    }
}
