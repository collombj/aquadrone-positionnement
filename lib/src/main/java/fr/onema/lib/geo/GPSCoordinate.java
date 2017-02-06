package fr.onema.lib.geo;

/**
 * Created by julien on 06/02/2017.
 */
public class GPSCoordinate {

    public final long lat;
    public final long lon;
    public final long alt;

    public GPSCoordinate(long lat, long lon, long alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }
}
