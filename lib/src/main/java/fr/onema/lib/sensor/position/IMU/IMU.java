package fr.onema.lib.sensor.position.IMU;

import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.sensor.Sensor;
import org.mavlink.messages.ardupilotmega.msg_attitude;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;

import java.util.Objects;

/**
 * Created by strock on 06/02/2017.
 * <p>
 * Classe qui représente la centrale inertielle embarquée
 */
public class IMU extends Sensor {
    private final Accelerometer accelerometer;
    private final Gyroscope gyroscope;
    private final Compass compass;

    /**
     * constructeur privé de l'imu
     *
     * @param accelerometer capteur
     * @param gyroscope     capteur
     * @param compass       capteur
     */
    private IMU(long timestamp, Accelerometer accelerometer, Gyroscope gyroscope, Compass compass) {
        super(timestamp);
        this.accelerometer = accelerometer;
        this.gyroscope = gyroscope;
        this.compass = compass;
    }

    /**
     * builder de l'imu a apartir du flux mavlink
     *
     * @param msg recuperation du flux mavlink
     * @return Un IMU instancié en fonction de messages MAVLink
     */
    public static IMU build(long timestamp, msg_scaled_imu msg, msg_attitude msgAttitude) {
        Objects.requireNonNull(msg);
        Objects.requireNonNull(msgAttitude);
        Accelerometer accelerometer = new Accelerometer(msg.xacc, msg.yacc, msg.zacc);
        Gyroscope gyroscope = new Gyroscope(msgAttitude.roll, msgAttitude.pitch, msgAttitude.yaw);
        Compass compass = new Compass(msg.xmag, msg.ymag, msg.zmag);
        return new IMU(timestamp, accelerometer, gyroscope, compass);
    }

    /**
     * Creation de l'imu  de simulation à parti de la classe  {@link GeoMaths}
     *
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


    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    public Gyroscope getGyroscope() {
        return gyroscope;
    }

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
