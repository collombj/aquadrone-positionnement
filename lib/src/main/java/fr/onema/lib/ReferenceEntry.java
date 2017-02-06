package fr.onema.lib;

public class ReferenceEntry {
    private int timestamp;
    private int lat;
    private int lon;
    private int alt;
    private int direction;
    private int temperature;

    ReferenceEntry(int timestamp, int lat, int lon, int alt, int direction, int temperature){
        this.timestamp = timestamp;
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.direction = direction;
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
}
