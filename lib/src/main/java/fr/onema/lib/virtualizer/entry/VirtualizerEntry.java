package fr.onema.lib.virtualizer.entry;

import fr.onema.lib.file.CSV;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;


public class VirtualizerEntry implements CSV {
    private final long timestamp;
    private int gpsLat;
    private int gpsLon;
    private int gpsAlt;
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
    public static final String HEADER = "timestamp,gpsLongitude,gpsLatitude,gpsAltitude,accelerationX,accelerationY,accelerationZ,rotationX,rotationY,rotationZ,capX,capY,capZ,pression,temperature";
    private final boolean hasGPS;

    /**
     * Constructeur de Virtualizer
     * @param timestamp Durée depuis 1er janvier 1970 en millisecondes
     * @param gpsLat Latitude du GPS
     * @param gpsLon Longitude du GPS
     * @param gpsAlt Altitude du GPS
     * @param xacc Acceleration en x
     * @param yacc Acceleration en y
     * @param zacc Acceleration en z
     * @param xgyro Rotation en x
     * @param ygyro Rotation en y
     * @param zgyro Rotation en z
     * @param xmag Orientation magnétique en x
     * @param ymag Orientation magnétique en y
     * @param zmag Orientation magnétique en z
     * @param pressure Pression
     * @param temperature Temperature
     */
    public VirtualizerEntry(long timestamp, int gpsLat, int gpsLon, int gpsAlt, short xacc, short yacc, short zacc, short xgyro, short ygyro, short zgyro, short xmag, short ymag, short zmag, float pressure, short temperature) {
        this.timestamp = timestamp;
        this.gpsLat = gpsLat;
        this.gpsLon = gpsLon;
        this.gpsAlt = gpsAlt;
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
        this.hasGPS = true;
    }

    /**
     * Constructeur de Virtualizer sans le GPS
     * @param timestamp Durée depuis 1er janvier 1970 en millisecondes
     * @param xacc Acceleration en x
     * @param yacc Acceleration en y
     * @param zacc Acceleration en z
     * @param xgyro Rotation en x
     * @param ygyro Rotation en y
     * @param zgyro Rotation en z
     * @param xmag Orientation magnétique en x
     * @param ymag Orientation magnétique en y
     * @param zmag Orientation magnétique en z
     * @param pressure Pression
     * @param temperature Temperature
     */
    public VirtualizerEntry(long timestamp, short xacc, short yacc, short zacc, short xgyro, short ygyro, short zgyro, short xmag, short ymag, short zmag, float pressure, short temperature) {
        this.timestamp = timestamp;
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
        this.hasGPS = false;
    }

    /**
     * Retourne le message GPS en format MavLink
     * @return GPSMAVLinkMessage
     */
    public msg_gps_raw_int getGPSMessage() {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = System.currentTimeMillis();
        msg.fix_type = 6;
        msg.lat = this.gpsLat;
        msg.lon = this.gpsLon;
        msg.alt = this.gpsAlt;
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
     * @return gpsLat
     */
    public int getGpsLat() {
        return gpsLat;
    }

    /**
     * Récupère la longitude du GPS
     * @return gpsLon
     */
    public int getGpsLon() {
        return gpsLon;
    }

    /**
     * Récupère l'altitude du GPS
     * @return gpsAlt
     */
    public int getGpsAlt() {
        return gpsAlt;
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

    /**
     * Récupère le boolean de la présence du GPS
     * @return boolean
     */
    public boolean getHasGPS() { return hasGPS; }

    /**
     * Renvoi une string des valeurs au format CSV
     * @return la chaine de caractère CSV
     */
    @Override
    public String toCSV() {
        return timestamp + "," + gpsLon + "," + gpsLat + "," + gpsAlt + "," + xacc + "," + yacc + "," + zacc + "," + xgyro + "," + ygyro + "," + zgyro + "," + xmag + "," + ymag + "," + zmag + "," + pressure + "," + temperature;
    }

     /**
      * Renvoi une string des champs au format CSV
      * @return la chaine de caractère CSV
      */
    @Override
    public String getCSVHeader() {
        return HEADER;
    }

}
