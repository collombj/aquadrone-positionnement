package fr.onema.lib.geo;

/**
 * Created by julien on 06/02/2017.
 */

/***
 * Classe permettant de modéliser des coordonnées gps (latitude, longitude, altitude)
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        GPSCoordinate that = (GPSCoordinate) o;
        if (lat != that.lat) return false;
        if (lon != that.lon) return false;
        return alt == that.alt;

    }

    @Override
    public int hashCode() {
        int result = (int) (lat ^ (lat >>> 32));
        result = 31 * result + (int) (lon ^ (lon >>> 32));
        result = 31 * result + (int) (alt ^ (alt >>> 32));
        return result;
    }
}
