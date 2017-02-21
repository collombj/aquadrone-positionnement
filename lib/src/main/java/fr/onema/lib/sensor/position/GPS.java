package fr.onema.lib.sensor.position;

import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.Sensor;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;

import java.util.Objects;

/***
 * Classe représentant les mesures GPS_SENSOR
 */
public class GPS extends Sensor {
    private static final String HEADER = "timestamp,latitude,longitude,altitude,direction";
    private GPSCoordinate position;
    private float direction;

    // TODO : maybe a single constructor is better
    private GPS(long timestamp, long lat, long lon, long alt) {
        super(timestamp);
        this.position = new GPSCoordinate(lat, lon, alt);
    }

    private GPS(long timestamp, long lat, long lon, long alt, float direction) {
        this(timestamp, lat, lon, alt);
        this.direction = direction;
    }

    /***
     * Builder de l'objet GPS_SENSOR
     * @param timestamp
     * @param msg Message correspondant à la mesure GPS_SENSOR en protocole MavLink
     */
    public static GPS build(long timestamp, msg_gps_raw_int msg) {
        Objects.requireNonNull(msg);
        return new GPS(timestamp, msg.lat, msg.lon, msg.alt, msg.cog);
    }

    /***
     * Builder de l'objet GPS_SENSOR
     * @param timestamp Heure de la mesure
     * @param lat Latitude de la mesure
     * @param lon Longitude de la mesure
     * @param alt Altitude de la mesure
     * @param direction Direction de la mesure
     */
    public static GPS build(long timestamp, long lat, long lon, long alt, float direction) {
        return new GPS(timestamp, lat, lon, alt, direction);
    }

    /**
     * Retourne les coordonnées de la mesure
     * @return Coordonnées de la mesure
     */
    public GPSCoordinate getPosition() {
        return position;
    }

    /***
     * Retourne l'orientation de la mesure en degrés
     * @return Orientation de la mesure
     */
    public float getDirection() {
        return direction;
    }

    @Override
    public String toCSV() {
        return getTimestamp() + "," + getPosition().lat + "," + getPosition().lon + "," + getPosition().alt + "," + getDirection();
    }

    @Override
    public String getCSVHeader() {
        return HEADER;
    }
}
