package fr.onema.lib.geo;

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
     * @param lat La latitude du relevé GPS
     * @param lon La longitude du relevé GPS
     * @param alt L'altitude du relevé GPS
     */
    public GPSCoordinate(long lat, long lon, long alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    @Override
    public String toString() {
        return "GPSCoordinate{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        GPSCoordinate that = (GPSCoordinate) o;
        if (lat != that.lat)
            return false;
        if (lon != that.lon)
            return false;
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
