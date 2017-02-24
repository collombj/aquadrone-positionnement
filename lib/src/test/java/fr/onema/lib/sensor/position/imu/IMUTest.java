package fr.onema.lib.sensor.position.imu;


import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_attitude;
import org.mavlink.messages.ardupilotmega.msg_raw_imu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IMUTest {

    @Test
    public void build() {
        msg_raw_imu msg = new msg_raw_imu();
        msg_attitude msgAttitude = new msg_attitude();
        IMU imu = IMU.build(27091994, msg, msgAttitude);
        assertNotNull(imu);
    }

    @Test
    public void getAccelerometer() {
        msg_raw_imu msg = new msg_raw_imu();
        msg_attitude msgAttitude = new msg_attitude();
        msg.xacc = 3;
        msg.yacc = 4;
        msg.zacc = 5;
        msgAttitude.roll = 1;
        msgAttitude.pitch = 1;
        msgAttitude.yaw = 1;
        IMU imu = IMU.build(27091994, msg, msgAttitude);
        assertNotNull(imu.getAccelerometer());
        assertEquals(3, imu.getAccelerometer().getxAcceleration());
        assertEquals(4, imu.getAccelerometer().getyAcceleration());
        assertEquals(5, imu.getAccelerometer().getzAcceleration());
    }

    @Test
    public void getGyroscope() throws Exception {
        msg_raw_imu msg = new msg_raw_imu();
        msg_attitude msgAttitude = new msg_attitude();
        msg.xgyro = 3;
        msg.ygyro = 4;
        msg.zgyro = 5;
        msgAttitude.roll = 1;
        msgAttitude.pitch = 1;
        msgAttitude.yaw = 1;
        IMU imu = IMU.build(27091994, msg, msgAttitude);
        assertNotNull(imu.getGyroscope());
        assertEquals(1.0, imu.getGyroscope().getRoll(), 0);
        assertEquals(1.0, imu.getGyroscope().getPitch(), 0);
        assertEquals(1.0, imu.getGyroscope().getYaw(), 0);
    }

    @Test
    public void getCompass() throws Exception {
        msg_raw_imu msg = new msg_raw_imu();
        msg_attitude msgAttitude = new msg_attitude();
        msg.xmag = 3;
        msg.ymag = 4;
        msg.zmag = 5;
        IMU imu = IMU.build(27091994, msg, msgAttitude);
        assertNotNull(imu.getCompass());
        assertEquals(3, imu.getCompass().getxMagnetic());
        assertEquals(4, imu.getCompass().getyMagnetic());
        assertEquals(5, imu.getCompass().getzMagnetic());
    }

    @Test
    public void getIMUVirtual() {
        CartesianVelocity refVelocity = new CartesianVelocity(2, 3, 4);
        GPSCoordinate prevCoordinate = new GPSCoordinate(5, 6, 7);
        GPSCoordinate coordinate = new GPSCoordinate(8, 9, 10);

        IMU imu = IMU.build(refVelocity, refVelocity, 1, 2);
        assertNotNull(imu.getAccelerometer());


    }
}