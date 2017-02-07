package fr.onema.lib.geo;

/**
 * Created by julien on 06/02/2017.
 */
// TODO : complete
public class GPSCoordinate {
    public final long lat;
    public final long lon;
    public final long alt;
    /**
     * Construit une coordonnée GPS (lat, lon, alt)
     *
     * @param lat la latitude
     * @param lon la longitude
     * @param alt l'altitude
     */
    public GPSCoordinate(long lat, long lon, long alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }
}
