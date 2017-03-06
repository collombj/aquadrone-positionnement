package fr.onema.lib.virtualizer.entry;


import fr.onema.lib.file.CSV;

/**
 * Classe représentant les entrées du fichier de référence
 */
public class ReferenceEntry implements CSV {
    public static final String HEADER = "timestamp,latitude,longitude,altitude,direction,temperature";
    private final long timestamp;
    private final int lat;
    private final int lon;
    private final int alt;
    private final float direction;
    private final int temperature;

    /**
     * Constructeur par défaut
     * @param timestamp   timestamp de la mesure
     * @param lat         la latitude de notre point
     * @param lon         la longitude de notre point
     * @param alt         l'altitude de notre point
     * @param direction   la direction que prend le point
     * @param temperature la température au niveau du point
     */
    public ReferenceEntry(long timestamp, int lat, int lon, int alt, float direction, int temperature) {
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

    /**
     * Récupère le timestamp
     * @return La valeur du timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter de la latidude
     * @return Valeur de la latitude
     */
    public int getLat() {
        return lat;
    }

    /**
     * Getter de la longitude
     * @return Valeur de la longitude
     */
    public int getLon() {
        return lon;
    }

    /**
     * Getter de l'altitude
     * @return Valeur de l'altitude
     */
    public int getAlt() {
        return alt;
    }

    /**
     * Getter de la direction
     * @return Valeur de la direction
     */
    public float getDirection() {
        return direction;
    }

    /**
     * Getter de la température
     * @return Valeur de la température
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * Format CSV modifié pour correspondance avec fichier de sortie computed
     * @return La représentation CSV pour fichier computed
     */
    String toCSVforComputedFormat() {
        return lat + "," + lon + "," + alt + "," + direction;
    }

    @Override
    public String toCSV() {
        return timestamp + "," + lat + "," + lon + "," + alt + "," + direction + "," + temperature;
    }

    @Override
    public String getCSVHeader() {
        return HEADER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ReferenceEntry that = (ReferenceEntry) o;

        if (timestamp != that.timestamp)
            return false;
        if (lat != that.lat)
            return false;
        if (lon != that.lon)
            return false;
        if (alt != that.alt)
            return false;
        if (Float.compare(that.direction, direction) != 0)
            return false;
        return temperature == that.temperature;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + lat;
        result = 31 * result + lon;
        result = 31 * result + alt;
        result = 31 * result + (Float.compare(direction, +0.0f) != 0 ? Float.floatToIntBits(direction) : 0);
        result = 31 * result + temperature;
        return result;
    }
}
