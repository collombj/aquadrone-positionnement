package fr.onema.lib.sensor.position;

import fr.onema.lib.geo.GPSCoordinate;
import org.mavlink.messages.ardupilotmega.msg_global_position_int;
import fr.onema.lib.sensor.*;
import java.util.Objects;

/**
 * Created by you on 06/02/2017.
 */

/***
 * Classe représentant les mesures GPS
 */
public class GPS extends Sensor {
    private GPSCoordinate position;
    private int direction;

    // TODO : maybe a single constructor is better
    private GPS(long timestamp, long lat, long lon, long alt) {
        super(timestamp);
        this.position = new GPSCoordinate(lat, lon, alt);
    }

    private GPS(long timestamp, long lat, long lon, long alt, int direction) {
        this(timestamp, lat, lon, alt);
        this.direction = direction;
    }

    /***
     * Builder de l'objet GPS
     * @param msg Message correspondant à la mesure GPS en protocole MavLink
     */
    public static GPS build(msg_global_position_int msg) {
        Objects.requireNonNull(msg);
        return new GPS(msg.time_boot_ms, msg.lat, msg.lon, msg.alt, msg.hdg);
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
    public int getDirection() {
        return direction;
    }

    @Override
    public String toCSV() {
        return getTimestamp() + "," + getPosition().lat + "," + getPosition().lon + "," + getPosition().alt + "," + getDirection();
    }

    @Override
    public String getCSVHeader() {
        return "timestamp, latitude, longitude, altitude, direction";
    }
}
