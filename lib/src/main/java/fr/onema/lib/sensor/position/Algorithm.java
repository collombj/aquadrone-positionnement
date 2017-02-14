package fr.onema.lib.sensor.position;

import fr.onema.lib.drone.Measure;
import fr.onema.lib.drone.Position;

import java.util.List;
import java.util.Objects;

/**
 * Recalcule l'ensemble des mesures et les entrées dans la base de données
 */
public abstract class Algorithm {
    private static final int MAX_ITERATIONS = 800;
    private boolean init = false;
    private GPS initGPS;

    /**
     * initialisation de la classe avec GPS
     *
     * @param gps position de base Gps
     */
    public void init(GPS gps) {
        Objects.requireNonNull(gps);
        initGPS = gps;
        init = true;
    }

    // TODO : complete
    /**
     * calcul les nouvelles positions et les positionne en base de données
     *
     * @param measures lis de measures de la plongée
     * @return si l'initialisation n'ets pas faite  true si l'ensemble est fait
     */
    public boolean calcul(List<Measure> measures) {
        /*
        if (init) {
            if (measures.size() > MAX_ITERATIONS) {
                for (int i = 0; i < measures.size(); i++) {
                    //correctPosition
                    //envoie à la base
                }
            }
        }
        */
        return init;
    }

    /**
     * Calcule les nouvelles positions selon l'algorithme
     * @param first  première position
     * @param defaut position de défaut
     */
    public void correctPosition(Position first, Position defaut, GPS gps) {;
    }

    // TODO : complete
    /**
     * Utilité à définir pour renvoyer la liste des nouvelles positions en cas de besoin
     */
    public void finish() {
    }


}
