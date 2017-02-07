package fr.onema.app.sensor.position;

import fr.onema.lib.geo.GPSCoordinate;
import org.mavlink.messages.ardupilotmega.msg_global_position_int;

import java.util.Objects;

/**
 * Created by you on 06/02/2017.
 */
public class GPS { // implements Sensor
    private GPSCoordinate position;
    private int direction;

    // TODO : maybe a single constructor is better
    private GPS(long timestamp, long lat, long lon, long alt) {
        // TODO : set timestamp to abstract
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

    // TODO : implement to CSV
}
