package fr.onema.lib.sensor;

import fr.onema.lib.file.CSV;

/**
 * Objet représentant l'état d'un capteur. En fonction de la nature de ce-dernier il pourra posséder des champs qui
 * lui sont propre.
 */
public abstract class Sensor implements CSV {
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
