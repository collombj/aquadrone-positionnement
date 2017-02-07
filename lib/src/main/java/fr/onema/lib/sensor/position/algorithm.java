package fr.onema.lib.sensor.position;

import fr.onema.lib.drone.Measure;
import fr.onema.lib.drone.Position;
import fr.onema.lib.sensor.position.IMU.IMU;

import java.util.List;


/**
 * Created by strock on 07/02/2017.
 * recalcul l'ensemble des measures et les entres dans la base de données
 */
public abstract class algorithm {

    private static int maxIterations = 800;
    private boolean init = false;
    private GPS initGPS;

    /**
     * initialisation de la classe avec GPS
     *
     * @param GPS position de base Gps
     */
    public void init(GPS GPS) {
        initGPS = GPS;
        init = true;
    }

    /**
     * calcul les nouvelles positions et les positionne en base de données
     *
     * @param measures lis de measures de la plongée
     * @return si l'initialisation n'ets pas faite  true si l'ensemble est fait
     */
    public boolean calcul(List<Measure> measures) {
        if (init == true) {


            if (measures.size() > maxIterations) {

                for (int i = 0; i < measures.size(); i++) {

                    //correctPosition

                    //envoie à la base
                }

            }
        }

        return init;
    }


    /**
     * calcul des nouvelles positions selon l'algorithme
     *
     * @param first  première position
     * @param defaut position de défaut
     * @return la nouvelle position
     */
    public Position correctPosition(Position first, Position defaut,GPS gps) {
        //to do
        return new Position();
    }

    /**
     * utilité à définir peut renvoyer la liste des nouvelles positions en cas de besoins
     */
    public void finish() {

        // to do ou enlever

    }


}
