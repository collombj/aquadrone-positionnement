package fr.onema.app.sensor.position;

import fr.onema.lib.geo.GPSCoordinate;
import org.mavlink.messages.ardupilotmega.msg_global_position_int;

import java.util.Objects;

/**
 * Created by you on 06/02/2017.
 */
public class GPS {
    private GPSCoordinate position;
    private int direction;

    public GPS(long timestamp, int lat, int lon, int alt) {
        Objects.requireNonNull(timestamp);
        Objects.requireNonNull(lat);
        Objects.requireNonNull(lon);
        Objects.requireNonNull(alt);
        this.position = new GPSCoordinate(lat, lon, alt);
    }

    public GPS(long timestamp, int lat, int lon, int alt, int direction) {
        this(timestamp, lat, lon, alt);
        this.direction = Objects.requireNonNull(direction);
    }

    public void build(msg_global_position_int msg) {
        Objects.requireNonNull(msg);
        this.position = new GPSCoordinate(msg.lat, msg.lon, msg.alt);
        this.direction = msg.hdg;
    }

    public GPSCoordinate getPosition() {
        return position;
    }

    public int getDirection() {
        return direction;
    }
}
