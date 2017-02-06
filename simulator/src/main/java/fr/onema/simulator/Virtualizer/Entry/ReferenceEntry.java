package fr.onema.simulator.Virtualizer.Entry;

public class ReferenceEntry /*implements CSV*/{
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
    ReferenceEntry(int timestamp, int lat, int lon, int alt, int direction, int temperature){
        if(timestamp < 0 || direction < 0){
            throw new IllegalArgumentException("Negative value for 'timestamp' or 'direction'");
        }else{
            this.timestamp =timestamp;
            this.direction = direction;
        }
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.temperature = temperature;
    }

    /**
     * On récupère la valeur de 'timestamp'
     * @return timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * On récupère la valeur de 'lat'
     * @return lat
     */
    public int getLat() {
        return lat;
    }

    /**
     * On récupère la valeur de 'lon'
     * @return lon
     */
    public int getLon() {
        return lon;
    }

    /**
     * On récupère la valeur de 'alt'
     * @return alt
     */
    public int getAlt() {
        return alt;
    }

    /**
     * On récupère la valeur de 'direction'
     * @return direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     * On récupère la valeur de 'temperature'
     * @return temperature
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * On crée puis récupère une chaîne de caractères représentant une entrée CSV
     * @return CSVString
     */
    public String toCSV() {
        return timestamp + "," + lat + "," + lon + "," + alt + "," + direction + "," + temperature;
    }
}
