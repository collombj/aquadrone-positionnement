package fr.onema.lib.sensor.position.IMU;

import org.mavlink.messages.ardupilotmega.msg_scaled_imu;

/**
 * Created by strock on 06/02/2017.
 */
public class IMU {
    private final Accelerometer accelerometer;
    private final Gyroscope gyroscope;
    private final Compass compass;

    private IMU(Accelerometer accelerometer, Gyroscope gyroscope, Compass compass) {
        this.accelerometer = accelerometer;
        this.gyroscope = gyroscope;
        this.compass = compass;
    }

    /**
     * Constructeur de l'IMU
     * @param msg recuperation du flux mavlink
     */
    public static IMU build (msg_scaled_imu msg){
        Accelerometer accelerometer = new Accelerometer(msg.xacc,msg.yacc,msg.zacc);
        Gyroscope gyroscope= new Gyroscope(msg.xgyro,msg.ygyro,msg.zgyro);
        Compass compass = new Compass (msg.xmag,msg.ymag,msg.zmag);
        return new IMU(accelerometer,gyroscope,compass);
    }

    // TODO : complete
    public  Accelerometer getAccelerometer() {
        return accelerometer;
    }

    // TODO : complete
    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    // TODO : complete
    public Compass getCompass() {
        return compass;
    }
}
