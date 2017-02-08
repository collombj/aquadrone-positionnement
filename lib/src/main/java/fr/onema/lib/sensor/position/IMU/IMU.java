package fr.onema.lib.sensor.position.IMU;

import fr.onema.lib.geo.CartesianCoordinate;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;

/**
 * Created by strock on 06/02/2017.
 */
public class IMU {
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
    private IMU(Accelerometer accelerometer, Gyroscope gyroscope, Compass compass) {
        this.accelerometer = accelerometer;
        this.gyroscope = gyroscope;
        this.compass = compass;
    }


    /**
     * builder de l'imu a apartir du flux mavlink
     *
     * @param msg recuperation du flux mavlink
     * @return
     */
    public static IMU build(msg_scaled_imu msg) {

        Accelerometer accelerometer = new Accelerometer(msg.xacc, msg.yacc, msg.zacc);
        Gyroscope gyroscope = new Gyroscope(msg.xgyro, msg.ygyro, msg.zgyro);
        Compass compass = new Compass(msg.xmag, msg.ymag, msg.zmag);

        return new IMU(accelerometer, gyroscope, compass);


    }

    /**
     * Creation de l'imu  de simulation à parti de la classe  {@link GeoMaths}
     *
     * @param refVelocity
     * @param prevTimestamp  timestamp de l'avant dernière mesure (ms)
     * @param prevCoordinate coodonnée de l'avant dernière mesure en format mavlink (degres decimaux 10^7 altitude m 10^3) {@link GPSCoordinate}
     * @param coordinate     coodonnée de l'avnt dernière mesure en format mavlink (degres decimaux 10^7 altitude m 10^3) {@link GPSCoordinate}
     * @param timestamp      timestamp de l'avant dernière mesure (ms)
     * @return un imu sans gyroscope et compas
     */
    public static IMU build(CartesianVelocity refVelocity, long prevTimestamp, GPSCoordinate prevCoordinate, long timestamp, GPSCoordinate coordinate) {


        CartesianCoordinate cartCoordinate = GeoMaths.computeCartesianPosition(prevCoordinate, coordinate);


        CartesianVelocity velocity = GeoMaths.computeVelocityFromCartesianCoordinate(cartCoordinate, timestamp - prevTimestamp);

        Accelerometer accelerometer = GeoMaths.computeAccelerometerData(refVelocity, velocity, timestamp - prevTimestamp);


        Gyroscope gyroscope = new Gyroscope(0, 0, 0);
        Compass compass = new Compass(0, 0, 0);


        return new IMU(accelerometer, gyroscope, compass);


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
}
