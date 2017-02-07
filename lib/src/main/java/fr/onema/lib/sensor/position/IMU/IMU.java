package fr.onema.lib.sensor.position.IMU;

import org.mavlink.messages.ardupilotmega.msg_scaled_imu;

/**
 * Created by strock on 06/02/2017.
 */
public class IMU {
    private final Accelerometer accelerometer;
    private final Gyroscope gyroscope;
    private final Compass compass;


    /**
     *constructeur privé de l'imu
     * @param accelerometer capteur
     * @param gyroscope capteur
     * @param compass capteur
     */
    private IMU(Accelerometer accelerometer, Gyroscope gyroscope, Compass compass) {
        this.accelerometer = accelerometer;
        this.gyroscope = gyroscope;
        this.compass = compass;
    }


    /**
     *builder de l'imu a apartir du flux mavlink
     * @param msg recuperation du flux mavlink
     * @return
     */
    public static IMU build (msg_scaled_imu msg){

        Accelerometer accelerometer = new Accelerometer(msg.xacc,msg.yacc,msg.zacc);
        Gyroscope gyroscope= new Gyroscope(msg.xgyro,msg.ygyro,msg.zgyro);
        Compass compass = new Compass (msg.xmag,msg.ymag,msg.zmag);

        return new IMU(accelerometer,gyroscope,compass);


    }

    /*public static IMU build (long timestamp,prevLat,prevLON,PrevAlt,prevDir,lat,lon, alt, dir){

      //to do

        return new IMU(accelerometer,gyroscope,compass);


    }*/


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
