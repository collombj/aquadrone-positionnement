package fr.onema.simulator.Virtualizer.Entry;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

/**
 * Created by Theo on 06/02/2017.
 */
public class VirtualizerEntry /* implements CSV */{
    private final long timestamp;
    private final int GPSLat;
    private final int GPSLon;
    private final int GPSAlt;
    private final short xacc;
    private final short yacc;
    private final short zacc;
    private final short xgyro;
    private final short ygyro;
    private final short zgyro;
    private final short xmag;
    private final short ymag;
    private final short zmag;
    private final float pressure;
    private final short temperature;

    /**
     * Constructeur de Virtualizer
     * @param timestamp
     * @param GPSLat
     * @param GPSLon
     * @param GPSAlt
     * @param xacc
     * @param yacc
     * @param zacc
     * @param xgyro
     * @param ygyro
     * @param zgyro
     * @param xmag
     * @param ymag
     * @param zmag
     * @param pressure
     * @param temperature
     */
    public VirtualizerEntry(long timestamp, int GPSLat, int GPSLon, int GPSAlt, short xacc, short yacc, short zacc, short xgyro, short ygyro, short zgyro, short xmag, short ymag, short zmag, float pressure, short temperature) {
        this.timestamp = timestamp;
        this.GPSLat = GPSLat;
        this.GPSLon = GPSLon;
        this.GPSAlt = GPSAlt;
        this.xacc = xacc;
        this.yacc = yacc;
        this.zacc = zacc;
        this.xgyro = xgyro;
        this.ygyro = ygyro;
        this.zgyro = zgyro;
        this.xmag = xmag;
        this.ymag = ymag;
        this.zmag = zmag;
        this.pressure = pressure;
        this.temperature = temperature;
    }

    /**
     * Retourne le message GPS en format MavLink
     * @return GPSMAVLinkMessage
     */
    public msg_gps_raw_int getGPSMessage() {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = System.currentTimeMillis();
        msg.fix_type = 6;
        msg.lat = this.GPSLat;
        msg.lon = this.GPSLon;
        msg.alt = this.GPSAlt;
        // Il me dit de mettre tous les bits à 1 si on connait pas la valeur
        msg.eph = Short.MAX_VALUE; // Dilution horizontale
        msg.epv = Short.MAX_VALUE; // Dilution verticale
        msg.vel = Short.MAX_VALUE; // vitesse sol calculée par le gps
        msg.cog = Short.MAX_VALUE; // Direction du mouvement
        msg.satellites_visible = 255;
        return msg;
    }

    /**
     * Retourne le message IMU en format MavLink
     * @return IMUMAVLinkMessage
     */
    public msg_scaled_imu getIMUMessage() {
        msg_scaled_imu msg = new msg_scaled_imu();
        msg.time_boot_ms = System.currentTimeMillis();
        msg.xacc = this.xacc;
        msg.yacc = this.yacc;
        msg.zacc = this.zacc;
        msg.xgyro = this.xgyro;
        msg.ygyro = this.ygyro;
        msg.zgyro = this.zgyro;
        msg.xmag = this.xmag;
        msg.ymag = this.ymag;
        msg.zmag = this.zmag;
        return msg;
    }

    /**
     * retourne le message de pression en format MavLink
     * @return PressureMAVLinkMessage
     */
    public msg_scaled_pressure getPressureMessage() {
        msg_scaled_pressure msg = new msg_scaled_pressure();
        msg.time_boot_ms = System.currentTimeMillis();
        msg.press_abs = this.pressure;
        msg.temperature = this.temperature;
        return msg;
    }

    /**
     * retourne le message de temperature en format MavLink
     * @return PressureMAVLinkMessage
     */
    public msg_scaled_pressure getTemperatureMessage() {
        msg_scaled_pressure msg = new msg_scaled_pressure();
        msg.time_boot_ms = System.currentTimeMillis();
        msg.press_abs = this.pressure;
        msg.temperature = this.temperature;
        return msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Récupère la latitude du GPS
     * @return GPSLat
     */
    public int getGPSLat() {
        return GPSLat;
    }

    /**
     * Récupère la longitude du GPS
     * @return GPSLon
     */
    public int getGPSLon() {
        return GPSLon;
    }

    /**
     * Récupère l'altitude du GPS
     * @return GPSAlt
     */
    public int getGPSAlt() {
        return GPSAlt;
    }

    /**
     * Récupère l'acceleration en X
     * @return xacc
     */
    public short getXacc() {
        return xacc;
    }

    /**
     * Récupère l'acceleration en Y
     * @return yacc
     */
    public short getYacc() {
        return yacc;
    }

    /**
     * Récupère l'acceleration en Z
     * @return zacc
     */
    public short getZacc() {
        return zacc;
    }

    /**
     * Récupère la vitesse de rotation en X
     * @return xgyro
     */
    public short getXgyro() {
        return xgyro;
    }

    /**
     * Récupère la vitesse de rotation en Y
     * @return ygyro
     */
    public short getYgyro() {
        return ygyro;
    }

    /**
     * Récupère la vitesse de rotation en Z
     * @return zgyro
     */
    public short getZgyro() {
        return zgyro;
    }

    /**
     * Récupère l'orientation magnétique en X
     * @return xmag
     */
    public short getXmag() {
        return xmag;
    }

    /**
     * Récupère l'orientation magnétique en Y
     * @return ymag
     */
    public short getYmag() {
        return ymag;
    }

    /**
     * Récupère l'orientation magnétique en Z
     * @return zmag
     */
    public short getZmag() {
        return zmag;
    }

    /**
     * Récupère la pression
     * @return pressure
     */
    public float getPressure() {
        return pressure;
    }

    /**
     * Récupère la temperature
     * @return temperature
     */
    public short getTemperature() {
        return temperature;
    }
}
