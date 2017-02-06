package fr.onema.simulator.Virtualizer.Entry;

import fr.onema.simulator.File.CSV;

/**
 * Created by Theo on 06/02/2017.
 */
public class ReferenceEntry implements CSV {
    private int timestamp;
    private int lat;
    private int lon;
    private int alt;
    private int direction;
    private int temperature;

    /**
     * récupère le timestamp
     * @return timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * récupère la latidude
     * @return Lat
     */
    public int getLat() {
        return lat;
    }

    /**
     * Récupère la longitude
     * @return Lon
     */
    public int getLon() {
        return lon;
    }

    /**
     * Récupère l'altitude
     * @return Alt
     */
    public int getAlt() {
        return alt;
    }

    /**
     * Récupère la direction
     * @return direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Récupère la température
     * @return temperature
     */
    public int getTemperature() {
        return temperature;
    }
}
