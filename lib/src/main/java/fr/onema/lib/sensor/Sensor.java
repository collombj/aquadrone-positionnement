package fr.onema.lib.sensor;

import fr.onema.lib.file.CSV;

/**
 * Objet représentant l'état d'un capteur. En fonction de la nature de ce-dernier il pourra posséder des champs qui
 * lui sont propre.
 * <p>
 * Created by loics on 06/02/2017.
 *
 * @author loics
 * @since 06-02-2017
 */
abstract public class Sensor implements CSV {
    /**
     * Champ représentant le timestamp d'une mesure issue d'un capteur.
     */
    long timestamp;

    /**
     * Construit un objet de type sensor.
     *
     * @param timestamp Indicateur temporel d'une mesure réalisé par un capteur à un temps T.
     */
    public Sensor(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Retourne l'heure de la mesure
     *
     * @return Le timestamp de la mesure
     */
    public long getTimestamp() {
        return timestamp;
    }
}
