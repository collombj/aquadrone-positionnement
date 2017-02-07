package fr.onema.lib.virtualizer.entry;


import fr.onema.lib.file.CSV;

public class ReferenceEntry implements CSV {
    private final long timestamp;
    private final int lat;
    private final int lon;
    private final int alt;
    private final float direction;
    private final short temperature;

    /**
     * Le constructeur de classe, pour attribuer une valeur aux attributs
     * @param timestamp timestamp de la mesure
     * @param lat la latitude de notre point
     * @param lon la longitude de notre point
     * @param alt l'altitude de notre point
     * @param direction la direction que prend le point
     * @param temperature la température au niveau du point
     */
    public ReferenceEntry(long timestamp, int lat, int lon, int alt, float direction, short temperature){
        if(timestamp < 0 || direction < 0){
            throw new IllegalArgumentException("Negative value for 'timestamp' or 'direction'");
        }else{
            this.timestamp = timestamp;
            this.direction = direction;
        }
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.temperature = temperature;
    }

    /**
     * récupère le timestamp
     * @return timestamp
     */
    public long getTimestamp() {
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
    public float getDirection() {
        return direction;
    }

    /**
     * Récupère la température
     * @return temperature
     */
    public int getTemperature() {
        return temperature;
    }

    @Override
    public String toCSV() {
        return null;
    }

    @Override
    public String getCSVHeader() {
        return null;
    }
}
