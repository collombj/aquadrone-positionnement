package fr.onema.lib.virtualizer.entry;

import fr.onema.lib.file.CSV;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;
import org.mavlink.messages.ardupilotmega.msg_attitude;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;


public class VirtualizerEntry implements CSV {
    public static final String HEADER = "timestamp,gpsLongitude,gpsLatitude,gpsAltitude,accelerationX,accelerationY,accelerationZ,roll,pitch,yaw,capX,capY,capZ,pression,temperature";
    private final long timestamp;
    private final int xacc;
    private final int yacc;
    private final int zacc;
    private final double roll;
    private final double pitch;
    private final double yaw;
    private final int xmag;
    private final int ymag;
    private final int zmag;
    private final float pressure;
    private final int temperature;
    private boolean hasGPS;
    private long gpsLat;
    private long gpsLon;
    private long gpsAlt;

    /**
     * Constructeur de Virtualizer
     * @param timestamp   Durée depuis 1er janvier 1970 en millisecondes
     * @param gpsLat      Latitude du GPS
     * @param gpsLon      Longitude du GPS
     * @param gpsAlt      Altitude du GPS
     * @param xacc        Acceleration en x
     * @param yacc        Acceleration en y
     * @param zacc        Acceleration en z
     * @param roll        roll du drone
     * @param pitch       pitch du drone
     * @param yaw         yaw du drone
     * @param xmag        Orientation magnétique en x
     * @param ymag        Orientation magnétique en y
     * @param zmag        Orientation magnétique en z
     * @param pressure    Pression
     * @param temperature Temperature
     */
    public VirtualizerEntry(long timestamp, long gpsLat, long gpsLon, long gpsAlt, int xacc, int yacc, int zacc, double roll, double pitch, double yaw, int xmag, int ymag, int zmag, float pressure, int temperature) {
        this(timestamp,xacc,yacc,zacc,roll,pitch,yaw,xmag,ymag,zmag,pressure,temperature);
        this.gpsLat = gpsLat;
        this.gpsLon = gpsLon;
        this.gpsAlt = gpsAlt;
        this.hasGPS = true;
    }

    /**
     * Constructeur de Virtualizer à partir de données GPS, IMU et Pressure.
     * @param gps       Objet GPS
     * @param imu       Objet IMU
     * @param pressure  Objet Pressure
     */
    public VirtualizerEntry(GPS gps, IMU imu, Pressure pressure) {
        if(gps == null) {
            this.gpsLat = 0;
            this.gpsLon = 0;
            this.gpsAlt = 0;
        } else {
            this.gpsLat = gps.getPosition().lat;
            this.gpsLon = gps.getPosition().lon;
            this.gpsAlt = gps.getPosition().alt;
        }
        if(imu == null) {
            this.xacc = 0;
            this.yacc = 0;
            this.zacc = 0;
            this.roll = 0;
            this.pitch = 0;
            this.yaw = 0;
            this.xmag = 0;
            this.ymag = 0;
            this.zmag = 0;
        } else {
            this.xacc = imu.getAccelerometer().getxAcceleration();
            this.yacc = imu.getAccelerometer().getyAcceleration();
            this.zacc = imu.getAccelerometer().getzAcceleration();
            this.roll = imu.getGyroscope().getRoll();
            this.pitch = imu.getGyroscope().getPitch();
            this.yaw = imu.getGyroscope().getYaw();
            this.xmag = imu.getCompass().getxMagnetic();
            this.ymag = imu.getCompass().getyMagnetic();
            this.zmag = imu.getCompass().getzMagnetic();
        }
        if(pressure == null) {
            this.pressure = 0;
            this.temperature = 0;
        } else {
            this.pressure = pressure.getAbsolute();
            this.temperature = pressure.getTemperature();
        }
        if(gps != null) {
            this.timestamp = gps.getTimestamp();
        } else if (pressure != null) {
            this.timestamp = pressure.getTimestamp();
        } else {
            this.timestamp = 0;
        }
        this.hasGPS = true;
    }

    /**
     * Constructeur de Virtualizer sans le GPS
     * @param timestamp   Durée depuis 1er janvier 1970 en millisecondes
     * @param xacc        Acceleration en x
     * @param yacc        Acceleration en y
     * @param zacc        Acceleration en z
     * @param roll        roll du drone
     * @param pitch       pitch du drone
     * @param yaw         yaw du drone
     * @param xmag        Orientation magnétique en x
     * @param ymag        Orientation magnétique en y
     * @param zmag        Orientation magnétique en z
     * @param pressure    Pression
     * @param temperature Temperature
     */
    public VirtualizerEntry(long timestamp, int xacc, int yacc, int zacc, double roll, double pitch, double yaw, int xmag, int ymag, int zmag, float pressure, int temperature) {
        this.timestamp = timestamp;
        this.xacc = xacc;
        this.yacc = yacc;
        this.zacc = zacc;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
        this.xmag = xmag;
        this.ymag = ymag;
        this.zmag = zmag;
        this.pressure = pressure;
        this.temperature = temperature;
        this.hasGPS = false;
    }

    /**
     * Retourne le message GPS en format MavLink
     *
     * @return GPSMAVLinkMessage
     */
    public msg_gps_raw_int getGPSMessage() {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = timestamp;
        msg.fix_type = 6;
        msg.lat = this.gpsLat;
        msg.lon = this.gpsLon;
        msg.alt = this.gpsAlt;
        msg.eph = Short.MAX_VALUE;
        msg.epv = Short.MAX_VALUE;
        msg.vel = Short.MAX_VALUE;
        msg.cog = Short.MAX_VALUE;
        msg.satellites_visible = 255;
        return msg;
    }

    /**
     * Retourne le message IMU en format MavLink
     *
     * @return IMUMAVLinkMessage
     */
    public msg_scaled_imu getIMUMessage() {
        msg_scaled_imu msg = new msg_scaled_imu();
        msg.time_boot_ms = System.currentTimeMillis();
        msg.xacc = this.xacc;
        msg.yacc = this.yacc;
        msg.zacc = this.zacc;
        msg.xgyro = 0;
        msg.ygyro = 0;
        msg.zgyro = 0;
        msg.xmag = this.xmag;
        msg.ymag = this.ymag;
        msg.zmag = this.zmag;
        return msg;
    }

    /**
     * Retourne le message Attitude en format MavLink
     *
     * @return AttitudeMessage
     */
    public msg_attitude getAttitudeMessage() {
        msg_attitude msgAttitude = new msg_attitude();
        msgAttitude.roll = (float) this.roll;
        msgAttitude.pitch = (float) this.pitch;
        msgAttitude.yaw = (float) this.yaw;
        return msgAttitude;
    }

    /**
     * retourne le message de pression en format MavLink
     *
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
     *
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
     *
     * @return gpsLat
     */
    public long getGpsLat() {
        return gpsLat;
    }

    /**
     * Récupère la longitude du GPS
     *
     * @return gpsLon
     */
    public long getGpsLon() {
        return gpsLon;
    }

    /**
     * Récupère l'altitude du GPS
     *
     * @return gpsAlt
     */
    public long getGpsAlt() {
        return gpsAlt;
    }

    /**
     * Récupère l'acceleration en X
     *
     * @return xacc
     */
    public int getXacc() {
        return xacc;
    }

    /**
     * Récupère l'acceleration en Y
     *
     * @return yacc
     */
    public int getYacc() {
        return yacc;
    }

    /**
     * Récupère l'acceleration en Z
     *
     * @return zacc
     */
    public int getZacc() {
        return zacc;
    }

    /**
     * Récupère la vitesse de rotation en X
     *
     * @return xgyro
     */
    public double getRoll() {
        return roll;
    }

    /**
     * Récupère la vitesse de rotation en Y
     *
     * @return ygyro
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * Récupère la vitesse de rotation en Z
     *
     * @return zgyro
     */
    public double getYaw() {
        return yaw;
    }

    /**
     * Récupère l'orientation magnétique en X
     *
     * @return xmag
     */
    public int getXmag() {
        return xmag;
    }

    /**
     * Récupère l'orientation magnétique en Y
     *
     * @return ymag
     */
    public int getYmag() {
        return ymag;
    }

    /**
     * Récupère l'orientation magnétique en Z
     *
     * @return zmag
     */
    public int getZmag() {
        return zmag;
    }

    /**
     * Récupère la pression
     *
     * @return pressure
     */
    public float getPressure() {
        return pressure;
    }

    /**
     * Récupère la temperature
     *
     * @return temperature
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * Récupère le boolean de la présence du GPS
     *
     * @return boolean
     */
    public boolean getHasGPS() {
        return hasGPS;
    }

    /**
     * Renvoi une string des valeurs au format CSV
     *
     * @return la chaine de caractère CSV
     */
    @Override
    public String toCSV() {
        return timestamp + "," + gpsLon + "," + gpsLat + "," + gpsAlt + "," + xacc + "," + yacc + "," + zacc + "," + roll + "," + pitch + "," + yaw + "," + xmag + "," + ymag + "," + zmag + "," + pressure + "," + temperature;
    }

    /**
     * Renvoi une string des champs au format CSV
     *
     * @return la chaine de caractère CSV
     */
    @Override
    public String getCSVHeader() {
        return HEADER;
    }

}
