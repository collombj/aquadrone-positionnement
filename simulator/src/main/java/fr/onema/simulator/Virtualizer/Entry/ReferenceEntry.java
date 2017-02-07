package fr.onema.simulator.Virtualizer.Entry;

import fr.onema.lib.file.CSV;

public class ReferenceEntry implements CSV {
    private final int timestamp;
    private final int lat;
    private final int lon;
    private final int alt;
    private final int direction;
    private final int temperature;

    /**
     * Le constructeur de classe, pour attribuer une valeur aux attributs
     * @param timestamp timestamp de la mesure
     * @param lat la latitude de notre point
     * @param lon la longitude de notre point
     * @param alt l'altitude de notre point
     * @param direction la direction que prend le point
     * @param temperature la température au niveau du point
     */
    public ReferenceEntry(int timestamp, int lat, int lon, int alt, int direction, int temperature) {
        if (timestamp < 0 || direction < 0) {
            throw new IllegalArgumentException("Negative value for 'timestamp' or 'direction'");
        } else {
            this.timestamp = timestamp;
            this.direction = direction;
        }
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.temperature = temperature;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getLat() {
        return lat;
    }

    public int getLon() {
        return lon;
    }

    public int getAlt() {
        return alt;
    }

    public int getDirection() {
        return direction;
    }

    public int getTemperature() {
        return temperature;
    }

    /**
     * Sortie au format CSV
     * @return CSVString
     */
    public String toCSV() {
        return timestamp + "," + lat + "," + lon + "," + alt + "," + direction + "," + temperature;
    }

    // TODO
    @Override
    public String getCSVHeader() {
        return null;
    }
}
