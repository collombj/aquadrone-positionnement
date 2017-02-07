package fr.onema.lib.sensor.position;

import fr.onema.lib.drone.Measure;
import fr.onema.lib.drone.Position;

import java.util.List;
import java.util.Objects;


/**
 * Created by strock on 07/02/2017.
 * recalcul l'ensemble des measures et les entres dans la base de données
 */
public abstract class Algorithm {

    private static int maxIterations = 800;
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
        if (init) {


            if (measures.size() > maxIterations) {

                for (int i = 0; i < measures.size(); i++) {

                    //correctPosition

                    //envoie à la base
                }

            }
        }

        return init;
    }

    // TODO : complete
    /**
     * calcul des nouvelles positions selon l'algorithme
     *
     * @param first  première position
     * @param defaut position de défaut
     * @return la nouvelle position
     */
    public Position correctPosition(Position first, Position defaut,GPS gps) {
        return new Position();
    }

    // TODO : complete
    /**
     * utilité à définir peut renvoyer la liste des nouvelles positions en cas de besoins
     */
    public void finish() {

        // to do ou enlever

    }


}
