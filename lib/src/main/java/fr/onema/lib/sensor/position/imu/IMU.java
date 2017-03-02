package fr.onema.lib.sensor.position.imu;

import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.sensor.Sensor;
import fr.onema.lib.tools.Configuration;
import org.mavlink.messages.ardupilotmega.msg_attitude;
import org.mavlink.messages.ardupilotmega.msg_raw_imu;

import java.io.FileNotFoundException;
import java.util.Objects;

/**
 * Classe qui représente la centrale inertielle embarquée
 */
public class IMU extends Sensor {
    private final Accelerometer accelerometer;
    private final Gyroscope gyroscope;
    private final Compass compass;

    /**
     * Constructeur
     * @param accelerometer capteur
     * @param gyroscope     capteur
     * @param compass       capteur
     */
    public IMU(long timestamp, Accelerometer accelerometer, Gyroscope gyroscope, Compass compass) {
        super(timestamp);
        this.accelerometer = accelerometer;
        this.gyroscope = gyroscope;
        this.compass = compass;
    }

    /**
     * Builder de l'imu a apartir du flux mavlink
     * @param msg recuperation du flux mavlink
     * @return Un IMU instancié en fonction de messages MAVLink
     */
    public static IMU build(long timestamp, msg_raw_imu msg, msg_attitude msgAttitude) throws FileNotFoundException {
        Objects.requireNonNull(msg);
        Objects.requireNonNull(msgAttitude);
        Configuration.AccelerationOffset offset = Configuration.getInstance().getOffset();
        Accelerometer accelerometer = new Accelerometer((int)Math.round(msg.xacc - offset.getAccelerationOffsetX()),
                (int)Math.round(msg.yacc - offset.getAccelerationOffsetY()),
                (int)Math.round(msg.zacc - offset.getAccelerationOffsetZ()));
        Gyroscope gyroscope = new Gyroscope(msgAttitude.roll, msgAttitude.pitch, msgAttitude.yaw);
        Compass compass = new Compass(msg.xmag, msg.ymag, msg.zmag);
        return new IMU(timestamp, accelerometer, gyroscope, compass);
    }

    /**
     * Creation de l'imu de simulation à partir de la classe  {@link GeoMaths}
     * @param previousVelocity vitesse précédente du drone
     * @param velocity         vitesse actuelle du drone
     * @param prevTimestamp    timestamp de l'avant dernière mesure (ms)
     * @param timestamp        timestamp de l'avant dernière mesure (ms)
     * @return un imu sans gyroscope et compas
     */
    public static IMU build(CartesianVelocity previousVelocity, CartesianVelocity velocity, long prevTimestamp, long timestamp) {
        Objects.requireNonNull(previousVelocity);
        Objects.requireNonNull(velocity);

        Accelerometer accelerometer = GeoMaths.computeAccelerometerData(previousVelocity, velocity, timestamp - prevTimestamp);
        Gyroscope gyroscope = new Gyroscope(0, 0, 0);
        Compass compass = new Compass(0, 0, 0);
        return new IMU(timestamp, accelerometer, gyroscope, compass);
    }

    /***
     * Getter de l'accéléromètre attaché à l'IMU
     * @return La représentation de l'accéléromètre associée
     */
    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    /***
     * Getter du gyroscope attaché à l'IMU
     * @return La représentation du gyroscope associé
     */
    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    /***
     * Getter du compas attaché à l'IMU
     * @return La représentation du compas associé
     */
    public Compass getCompass() {
        return compass;
    }

    @Override
    public String toCSV() {
        // TODO to be implemented
        return null;
    }

    @Override
    public String getCSVHeader() {
        // TODO to be implemented
        return null;
    }
}
